# Icebase
Lightweight, fast and simple real-time database

## General
Icebase is build on top of spring boot, to run on every desired host OS (Windows, Linux, MacOS). Icebase will use MongoDB for data storage.

For more details, look at the wiki pages (https://github.com/Surras/Icebase/wiki)

## Concept
Icebase manage multiple projects. Each project holds its own database. You can use simple CRUD functions to manipulate your data in your project. Due it's function of a real-time database you can simply observe data in your project. The subscription works via web sockets. If a client subscribes a node, this node is will be listed in-memory and broadcast all changes to its subscribers (add, change, delete events).

The data storage functionality can be restricted by rules. These rules are server-side configured and validate each incoming data.

It is possible to store single-object data in a node, or collections. If a node is used as a collection, every item in this collection get a unique key. Collections can be restricted by a type-shema, so it is possible to only push one data type to this collection and avoid type-mixing.

In Icebase it is possible to write own modules for data manipulation or other functionality. These modules can hook up based on your project and need to be annotated in your config-file of your project. A module will be worked like a proxy for your data. It is possible to catch the hole data stream, or just get a copy of them (for read-only operations). The second one is for better performance

## Modules
If you wish to manipulate your data stream, you easily can create your own modules. A module is working like a proxy for your project. Every module must hooked up in your config file, and can be chained with other modules in a row, so you can create complex business logic.

## Limitations
Icebase is using MongoDB as Database, so you only can use Icebase with the limitations of MongoDB to store your data. If you break these limits, it is not possible to store more data in that way. so please be aware and use your Icebase wisely.

Limitations (https://docs.mongodb.com/manual/reference/limits/):
* Project names are limit to 64 characters. Each project generate it's own database and database name length is limit to 64 characters
* the size of one single document (object) is limit to 16 Megabytes. If you nest documents, every nested document have "it's own size limit" and have no impact of the parent document limit
* Maximum nesting level of documents is 100. Allthough it is possible to nest document at such a deep level, please keep in mind that performance are gain from flat structures. Avoid deep nesting, if possible.
* By default, each datase have an maximum size limit of 32 Terabytes, based on the limitations of the MMAPv1 storage engine


