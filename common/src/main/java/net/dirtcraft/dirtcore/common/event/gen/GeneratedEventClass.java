/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.event.gen;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.dirtcraft.dirtcore.api.DirtCore;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.util.Param;
import net.dirtcraft.dirtcore.common.cache.LoadingMap;
import net.dirtcraft.dirtcore.common.event.EventDispatcher;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * Holds the generated event class for a given type of {@link DirtCoreEvent}.
 */
public class GeneratedEventClass {

    /**
     * A loading cache of event types to {@link GeneratedEventClass}es.
     */
    private static final Map<Class<? extends DirtCoreEvent>, GeneratedEventClass> CACHE =
            LoadingMap.of(clazz -> {
                try {
                    return new GeneratedEventClass(clazz);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
    /**
     * A method handle for the constructor of the event class.
     */
    private final MethodHandle constructor;
    /**
     * An array of {@link MethodHandle}s, which can set values for each of the properties in the
     * event class.
     */
    private final MethodHandle[] setters;

    private GeneratedEventClass(final Class<? extends DirtCoreEvent> eventClass) throws Throwable {
        // get a TypeDescription for the event class
        final TypeDescription eventClassType = new TypeDescription.ForLoadedType(eventClass);

        // determine a generated class name of the event
        final String eventClassSuffix =
                eventClass.getName().substring(DirtCoreEvent.class.getPackage().getName().length());
        final String packageWithName = GeneratedEventClass.class.getName();
        final String generatedClassName =
                packageWithName.substring(0, packageWithName.lastIndexOf('.')) + eventClassSuffix;

        DynamicType.Builder<AbstractEvent> builder = new ByteBuddy(ClassFileVersion.JAVA_V8)
                // create a subclass of AbstractEvent
                .subclass(AbstractEvent.class,
                        ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                // using the predetermined generated class name
                .name(generatedClassName)
                // implement the event interface
                .implement(eventClassType)
                // implement all methods annotated with Param by simply returning the value from
                // the corresponding field with the same name
                .method(isAnnotatedWith(Param.class))
                .intercept(FieldAccessor.of(NamedElement.WithRuntimeName::getInternalName))
                // implement DirtCoreEvent#getEventType by returning the event class type
                .method(named("getEventType").and(returns(Class.class)).and(takesArguments(0)))
                .intercept(FixedValue.value(eventClassType))
                // implement AbstractEvent#mh by calling & returning the value of MethodHandles
                // .lookup()
                .method(named("mhl").and(returns(MethodHandles.Lookup.class))
                        .and(takesArguments(0)))
                .intercept(MethodCall.invoke(MethodHandles.class.getMethod("lookup")))
                // implement a toString method
                .withToString();

        // get a sorted array of all methods on the event interface annotated with @Param
        final Method[] properties = Arrays.stream(eventClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Param.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Param.class).value()))
                .toArray(Method[]::new);

        // for each property, define a field on the generated class to hold the value
        for (final Method method : properties) {
            builder = builder.defineField(method.getName(), method.getReturnType(),
                    Visibility.PRIVATE);
        }

        // finish building, load the class, get a constructor
        final Class<? extends AbstractEvent> generatedClass =
                builder.make().load(GeneratedEventClass.class.getClassLoader()).getLoaded();
        this.constructor = MethodHandles.publicLookup().in(generatedClass)
                .findConstructor(generatedClass, MethodType.methodType(void.class, DirtCore.class))
                .asType(MethodType.methodType(AbstractEvent.class, DirtCore.class));

        // create a dummy instance of the generated class & get the method handle lookup instance
        final MethodHandles.Lookup lookup =
                ((AbstractEvent) this.constructor.invoke((Object) null)).mhl();

        // get 'setter' MethodHandles for each property
        this.setters = new MethodHandle[properties.length];
        for (int i = 0; i < properties.length; i++) {
            final Method method = properties[i];
            this.setters[i] =
                    lookup.findSetter(generatedClass, method.getName(), method.getReturnType())
                            .asType(MethodType.methodType(void.class,
                                    new Class[] {AbstractEvent.class, Object.class}));
        }
    }

    /**
     * Generate a {@link GeneratedEventClass} for the given {@code event} type.
     *
     * @param event the event type
     * @return the generated class
     */
    public static GeneratedEventClass generate(final Class<? extends DirtCoreEvent> event) {
        return CACHE.get(event);
    }

    /**
     * Pre-generates {@link GeneratedEventClass}es for known event types.
     */
    public static void preGenerate() {
        for (final Class<? extends DirtCoreEvent> eventType :
                EventDispatcher.getKnownEventTypes()) {
            generate(eventType);
        }
    }

    /**
     * Creates a new instance of the event class.
     *
     * @param api        an instance of the DirtCore API
     * @param properties the event properties
     * @return the event instance
     * @throws Throwable if something goes wrong
     */
    public DirtCoreEvent newInstance(final DirtCore api,
            final Object... properties) throws Throwable {
        if (properties.length != this.setters.length) {
            throw new IllegalStateException(
                    "Unexpected number of properties. given: " + properties.length + ", expected: "
                            + this.setters.length);
        }

        // create a new instance of the event
        final AbstractEvent event = (AbstractEvent) this.constructor.invokeExact(api);

        // set the properties onto the event instance
        for (int i = 0; i < this.setters.length; i++) {
            final MethodHandle setter = this.setters[i];
            final Object value = properties[i];
            setter.invokeExact(event, value);
        }

        return event;
    }
}
