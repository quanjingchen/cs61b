# Gitlet Design Document

**Name**: Quanjing Chen

## Classes and Data Structures

### Commit

#### Instance Variables

1. message - [string] contains the message of a commit
2. timeStamp - [string] time at which a commit was created
3. parent - [string] the parent commit of a commit object
4. tree - [string] directory structures mapping names to references to blobs and other trees


### Blob
#### Instance Variables

1. name - [string]  ??
#### Method

1. readBlob - read blob from the object folder
2. writeBlob - save a blob to the object folder for future use
3. compareBlob ??
4. addBlob - Add blob to the staging area ???

### Repository

#### Instance Variables

1. Head - [string] path to the current commit (e.g., refs/heads/master, refs/heads/branch1)
2. Branch - [string] the active branch

#### Method

1. init - initialize a repository; initialize a master branch
2. add - push a blob to the staging area (write filename to the staging file) ??
3. commit
4. getHead - read head pointer from the refs folder 
5. writeHead - save head pointer to the refs folder

### Head
#### Instance Variables

### Branch
#### Instance Variables

## Algorithms

## Persistence

