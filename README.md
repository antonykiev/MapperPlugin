
# **MapCraft** plugin for mapping object

[![Android Studio Plugin](https://img.shields.io/badge/plugin-AndroidStudio-green.svg)](https://plugins.jetbrains.com/plugin/26185-mapcraft)
[![IntelliJ Idea Plugin](https://img.shields.io/badge/plugin-IntelliJ%20%20Idea-purple.svg)](https://plugins.jetbrains.com/plugin/26185-mapcraft)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26185-mapcraft.svg)](https://plugins.jetbrains.com/plugin/26185-mapcraft)
[![Version](https://img.shields.io/jetbrains/plugin/v/26185.svg?label=version)](https://plugins.jetbrains.com/plugin/26185-mapcraft)

### Description

The MapCraft plugin is a productivity tool for IntelliJ IDEA and Android Studio designed to streamline the creation of object-to-object mappers in your Kotlin or Java projects. Whether you need to map between DTOs, entities, models, or custom objects, this plugin automates the tedious process of writing boilerplate mapping code.
Key Features:

- Automatic Mapper Generation: Generate mapper functions with just a few clicks.
- Easy-to-Use UI: A user-friendly interface for selecting source and target     objects and fine-tuning the mappings.

Benefits:

- Save time and reduce boilerplate when working with complex models.
- Automatic mapping of nested classes.
- Improve code quality by eliminating human errors in manual mapping.
- Focus on business logic rather than repetitive tasks.

Ideal For:

- Backend developers working with data transformation layers.
- Android developers mapping between network, domain, and UI models.
- Any developer tired of writing repetitive mapper code manually.    

Boost your development efficiency and eliminate mapping headaches with MapCraft!

### How to install

Go File -> Settings -> Plugins -> Enter in search field MapCraft -> Install

### Supported IDE

Supported Products
Android Studio — Android Studio Ladybug  2024.2.1, Jellyfish | 2023.3.1+
Aqua — 2024.1.1+
IntelliJ IDEA Community — 2023.3+
IntelliJ IDEA Ultimate — 2023.3+

### How to use

Let's say you have data classes

<img src="images/step1.png" alt="Alt Text" width="400">

Right click on the class you want to be source of mapping. And push button  Generate Mapper

<img src="images/step2.png" alt="Alt Text" width="400">

Push button ellipsis to select target class

<img src="images/step3.png" alt="Alt Text" width="400">

Select target class

<img src="images/step4.png" alt="Alt Text" width="400">

You can see generated function

<img src="images/step5.png" alt="Alt Text" width="400">
