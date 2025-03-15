# DirtCore Demo

---

### NOTICE

This is an open-source demo version of DirtCore for educational purposes. Many of the core
functionalities have been removed or replaced with a dummy. DirtCore is a mod that allows hosting
Minecraft servers with versions ranging from 1.7.10 (Java 8) - 1.21.1 (Java 17), providing the same
utilities. DirtCore is a closed-source project I am developing for the modded Minecraft network
DirtCraft.

---

DirtCore is a utility addon for Minecraft servers. It combines Discord integration, limits,
restrictions, economy, crates, kits, chat formatting, lag prevention, voting, punishments and
many utilities in one mod, usable for any Minecraft version.

The wiki can be found
here: [DirtCore Wiki WIP](https://github.com/AlphaConqueror/DirtCore-Demo/wiki)

### Index

* [Project Structure](#project-structure)
* [Building the Project](#building-the-project)
* [Database Setup](#database-setup)
* [Project Layout](#project-layout)
* [Credits](#credits)
* [License](#license)

---

## Project Structure

The project structure is special due to different ForgeGradle versions supporting different Java and
Gradle versions.
The root project contains the modules that are used globally. The gradle projects `latest`
and `legacy` are not included in the root project, but rather include the root project itself.

## Building the Project

DirtCore uses Gradle to handle dependencies & building.

#### Requirements

* Java 17 JDK
* Git

#### Environment File

DirtCore uses public and private dependencies hosted on GitHub. For them to be downloaded, a `.env`
file with GitHub
credentials is needed:

```
# your GitHub username
USERNAME=<YOUR_NAME>
# your GitHub personal access token (https://github.com/settings/tokens)
# min permissions: repo, read:packages
TOKEN=<YOUR_ACCESS_TOKEN>
```

Create a file with the contents above named `.env` and replace the placeholders `<YOUR_NAME>`
and `<YOUR_ACCESS_TOKEN>`
with
your GitHub username and your GitHub access token created here:
[github.com/settings/tokens](https://github.com/settings/tokens). The token needs the
permissions `repo`
and `read:packages`. This file is excluded by default from commits, **do not** force it to be
included.

#### Compiling from source

```sh
git clone https://github.com/AlphaConqueror/DirtCore-Demo.git
cd DirtCore-Demo/
./gradlew build
```

You can find the output jars in the `loader/build/libs` or `build/libs` directories of each
Minecraft version
respectively.

## Database Setup

DirtCore uses MariaDB as the main database.

* Download the [latest MariaDB server version](https://mariadb.org/download/).
* Install the MariaDB server. Here is the getting started guide for MariaDB:
  [mariadb.com/kb/en/getting-installing-and-upgrading-mariadb/](https://mariadb.com/kb/en/getting-installing-and-upgrading-mariadb/)
* Open the MariaDB client terminal, enter your password and create a database called `dirtcore`:
  ```mariadb
  CREATE DATABASE dirtcore;
  ```
  The tables will be created automatically.
* To display the database, tables and contents, I recommend using
  [MySQL Workbench](https://dev.mysql.com/downloads/workbench/).

## Project Layout

The project is split up into a few separate modules:

* **API** - The semantically versioned API used by other addons wishing to integrate with and
  retrieve data
  from DirtCore. This module (for the most part) does not contain any implementation itself, and is
  provided by the
  addon.
* **Common** - The common module contains most of the code which implements the respective DirtCore
  addons. This
  abstract module reduces duplicated code throughout the project.
* **Platform Specific (Forge, NeoForge, ~~Bukkit~~, ...)** - Each use the common module to implement
  addons on
  the respective
  server platforms.

## Credits

* [LuckPerms](https://github.com/LuckPerms/LuckPerms) - The initial framework of DirtCore is based
  on the LuckPerms framework, but has been heavily modified since to match our needs and to be able
  to support legacy Minecraft versions, like Forge 1.7.10.
* [Kyori Pagination](https://github.com/KyoriPowered/adventure-text-feature-pagination) - DirtCore
  uses a modified version of the pagination utils for our textual pagination needs.
* [Kyori Team](https://github.com/kyoripowered) - Special thanks to the Kyori team and all
  contributors for providing so many helpful libraries.

## License

DirtCore is licensed under the MIT license.
