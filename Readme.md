# Taskinator

A lightweight, terminal-based task manager written in pure Java — no external libraries. Tasks are persisted to a local `tasks.json` file using a hand-rolled JSON lexer, parser, and serializer built from scratch.

---

## Features

- Add, update, and delete tasks from the terminal
- Mark tasks as **In Progress** or **Done**
- Filter and list tasks by status
- Automatic task ID re-sequencing after deletion
- Zero dependencies — custom JSON engine handles all serialization and persistence

---

## Project Structure

```
Taskinator/
├── JsonParser/
│   ├── JsonLexer.java        # Tokenizes raw JSON strings
│   ├── JsonParser.java       # Parses token stream into Java Maps
│   ├── JsonSerializer.java   # Serializes Java objects back to JSON
│   ├── Token.java            # Token data class
│   └── TokenType.java        # Token type enum
├── Taskinator.java           # Main application entry point
└── tasks.json                # Persistent task storage (auto-created)
```

---

## Requirements

- Java 21 or higher

---

## Getting Started

**1. Clone the repository**

```bash
git clone https://github.com/your-username/Taskinator.git
cd Taskinator
```

**2. Compile the source files**

Compile the `JsonParser` package first, then the main class:

```bash
javac JsonParser/*.java
javac Taskinator.java
```

**3. Run**

```bash
java Taskinator [options] [task]
```

---

## Usage

```
Usage: java Taskinator [options] [task]
```

| Flag | Alias | Description |
|------|-------|-------------|
| `-a` | `--add` | Add a new task |
| `-u` | `--update` | Update an existing task's description |
| `-d` | `--delete` | Delete a task |
| `-m` | `--mark` | Update a task's status |
| `-l` | `--list` | List all tasks |
| `-ld` | `--list-done` | List completed tasks |
| `-lnd` | `--list-not-done` | List pending tasks |
| `-lp` | `--list-progress` | List in-progress tasks |

---

## Examples

```bash
# Add a new task
java Taskinator -a "Learn Java"
java Taskinator --add "Build a CLI app"

# Update a task description (prompts for task ID)
java Taskinator -u "Learn advanced Java"

# Delete a task (prompts for task ID)
java Taskinator -d

# Mark a task as In Progress or Done (prompts for task ID and choice)
java Taskinator -m

# List all tasks
java Taskinator -l

# List only completed tasks
java Taskinator -ld

# List only pending tasks
java Taskinator -lnd

# List only in-progress tasks
java Taskinator -lp
```

---

## Data Storage

Tasks are stored in `tasks.json` in the working directory and are automatically created on first run. The file follows this structure:

```json
{
  "1": {
    "task": "Learn Java",
    "progress": "TODO",
    "created-at": "18/06/2026"
  },
  "2": {
    "task": "Build a CLI app",
    "progress": "In Progress",
    "created-at": "18/06/2026"
  }
}
```

Valid progress values are `TODO`, `In Progress`, and `Done`.

---

## How It Works

Taskinator includes a purpose-built JSON engine in the `JsonParser` package:

- **`JsonLexer`** — Scans the raw JSON string character by character and emits a flat list of typed tokens (`BEGIN_OBJ`, `END_OBJ`, `STRING`, `NAME_SEP`, `VALUE_SEP`, `EOF`).
- **`JsonParser`** — Consumes the token stream and recursively constructs a `Map<String, Object>` representing the task data.
- **`JsonSerializer`** — Converts the in-memory map back into a valid JSON string, handling strings, numbers, booleans, collections, arrays, and proper escape sequences.

This means Taskinator has **no third-party dependencies** and requires only the Java standard library.

---

## License

This project is open source. Feel free to use, modify, and distribute it.
