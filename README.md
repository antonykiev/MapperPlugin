
# MapCraft plugin for mapping object

### Description
MapCraft is a new IntelliJ Idea and Android Studio plugin designed to streamline the process of generating mapping code for your projects. With MapCraft, you can automate the creation of mapping functions, reducing boilerplate and saving development time.

Key Features:
- does not affect build speed of project. Generated once and forgotten;
- simplifies the generation of mappers for data transformation;
- designed to work seamlessly with Kotlin and Java projects;
- lightweight and easy to integrate into your workflow;

***Note***: *This plugin is currently in an unstable version, meaning it may have bugs or limited functionality. I am actively improving it, and your feedback is invaluable to me.*

### Example of integration into the project

- install plugin
- create setting json file (where the file is located and file name
  does not matter)
- go to **Code -> Generate Mappers**
- wait a couple of seconds because IDE  does not immediately update the files in project
  For example mapper_instruction.json
```json
{
  "outputDir": "src",
  "mapperName": "mapper.CustomMapper",
  "mappingRules": {
    "org.mapper.generator.PersonResponse": {
      "target": "org.mapper.generator.Person"
    },
    "org.mapper.generator.GroupResponse": {
      "target": "org.mapper.generator.Group",
      "details": [
        "id = source.id.toInt()"
      ]
    },
    "org.mapper.generator.Person": {
      "target": "org.mapper.generator.PersonResponse"
    },
    "org.mapper.generator.Group": {
      "target": "org.mapper.generator.GroupResponse",
      "details": [
        "id = source.id.toLong()"
      ]
    },
    "org.mapper.generator.ManagerResponse": {
      "target": "org.mapper.generator.Manager"
    },
    "org.mapper.generator.CafeResponse": {
      "target": "org.mapper.generator.Cafe",
      "details": [
        "manager = mapManagerResponseToManager(source.manager)"
      ]
    }
  }
}
```
**outputDir** *(optional)* - is the full path to the folder for generating the mapper. If the field is not specified, the mapper file will be generated at **`com.plugin.default`** directory;

**mapperName** *(optional)* - is the full name of your mapper. If the field is not specified, the mapper file name will be generated with **`Mapper`** name;

**mappingRules** *(mandatory)* - in this block is configured source object and target object also with full names. In the example above it is stated that **`org.mapper.generator.PersonResponse`** (source) will be mapped to **`org.mapper.generator.Person`** (target);

**details** *(optional)* - here is specified custom field mapping. Here are specified any specific rules for mapping;

### Naming of mapping methods and mapping of nested objects
Methods for mapping objects are generated according to the following rule
**map** + *short source class name* + **to** + *short target class name*
An example of mapping with nested objects can be seen in `org.mapper.generator.CafeResponse -> org.mapper.generator.Cafe`

###Plans

What needs to be done

- add fields validations;
- parameters for method map;
- remove the need to specify the full class name;
- simplify the interface for simple cases;
- on UI add a button to open the generated object;
- adapt to multi-module project;
