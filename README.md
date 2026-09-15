# cyoa

It's a [choose you own adventure](https://en.wikipedia.org/wiki/Choose_Your_Own_Adventure) (CYOA) web application inspired from https://github.com/madelyneriksen/yaml-adventure.

Co-developed with AI

## Prerequisites

- Java 21 or newer
- A shell with permission to run the Maven Wrapper

The project includes the Maven Wrapper, so a separate Maven installation is
not required.

## Build and test

From the project root, run:

```bash
./mvnw clean verify
```

This generates the OpenAPI sources, compiles the application, and runs the
test suite.

On Windows, use the batch wrapper instead:

```bat
mvnw.cmd clean verify
```

## Start the application

To start the application directly with Maven:

```bash
./mvnw spring-boot:run
```

Alternatively, build the packaged jar and run it:

```bash
./mvnw clean package
java -jar target/create-your-own-adventure-0.0.1-SNAPSHOT.jar
```

Once the application starts, open [http://localhost:8080/](http://localhost:8080/)
in a browser.

## Play an adventure

After creating a CYOA JSON file, upload it to the application before playing
it. Open the application at [http://localhost:8080/](http://localhost:8080/),
choose the JSON file on the upload page, and submit it. The application will
validate the adventure and start the game when the file is valid.

## Generate a Commodore 64 BASIC game

After a JSON adventure has been uploaded and validated, the start page includes
a **Download C64 BASIC game** link. Select it to download the adventure as
`adventure.bas`, a plain-text Commodore 64 BASIC program.

The generated program includes the adventure text, numbered choices, input
handling, and navigation between locations. Locations without options are
written as ending locations. The JSON adventure must be valid before the BASIC
program can be generated.

## Write a CYOA adventure

Adventures are JSON files with an `id`, a description, a starting location,
and a list of locations. Each location has an `id` and the text shown to the
player. Locations can provide options that point to another location by its
ID.

```json
{
	"id": "forest-adventure",
	"description": "Find your way through the forest.",
	"startLocationId": "forest-entrance",
	"locations": [
		{
			"id": "forest-entrance",
			"text": "You reach a forest entrance. A path leads north.",
			"options": [
				{
					"text": "Follow the path",
					"locationId": "clearing"
				}
			]
		},
		{
			"id": "clearing",
			"text": "You arrive at a quiet clearing. Your adventure ends here."
		}
	]
}
```

### JSON fields

- `id`: Unique identifier for the adventure.
- `description`: Short description of the adventure.
- `startLocationId`: ID of the location shown when the adventure starts. It
	must match one of the location IDs in `locations`.
- `locations`: List of locations in the adventure. Each location must have a
	unique `id` and `text`.
- `locations[].options`: Optional list of choices. Each option requires `text`
	and a `locationId` that matches another location's ID.

To create an ending, omit `options` from a location. The complete example in
`src/main/resources/example-adventures/test.json` can also be used as a
starting point.