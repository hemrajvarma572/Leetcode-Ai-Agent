import os
import json
import re
import requests
import subprocess
import tempfile
import shutil

from problem_selector import select_problem


GEMINI_URL = (
    "https://generativelanguage.googleapis.com/v1beta/"
    "models/gemini-3.1-flash-lite:generateContent"
)

SOLVED_FILE = "data/solved.json"

DATABASE_BASE = (
    "https://raw.githubusercontent.com/"
    "mcaupybugs/leetcode-problems-db/master/problems/"
)

MAX_FIX_ATTEMPTS = 3


def load_solved():
    try:
        with open(SOLVED_FILE, "r") as file:
            return set(json.load(file).get("solved", []))
    except FileNotFoundError:
        return set()


def save_solved(slug):
    with open(SOLVED_FILE, "r") as file:
        data = json.load(file)

    if slug not in data["solved"]:
        data["solved"].append(slug)

    with open(SOLVED_FILE, "w") as file:
        json.dump(data, file, indent=2)


def get_full_problem(problem):
    problem_id = int(problem["id"])
    slug = problem["slug"]

    filename = f"{problem_id:04d}-{slug}.json"
    url = DATABASE_BASE + filename

    print("Downloading full problem...")
    print(url)

    response = requests.get(url, timeout=30)

    if response.status_code != 200:
        raise Exception(
            f"Could not download problem data: HTTP {response.status_code}"
        )

    data = response.json()

    if not isinstance(data, dict):
        raise Exception("Invalid problem data")

    return data


def clean_java_code(text):
    text = re.sub(r"```java", "", text, flags=re.IGNORECASE)
    text = re.sub(r"```", "", text)

    return text.strip()


def call_gemini(prompt):
    api_key = os.environ.get("GEMINI_API_KEY")

    if not api_key:
        raise Exception("GEMINI_API_KEY is not configured")

    response = requests.post(
        GEMINI_URL,
        params={"key": api_key},
        json={
            "contents": [
                {
                    "parts": [
                        {
                            "text": prompt
                        }
                    ]
                }
            ]
        },
        timeout=60
    )

    response.raise_for_status()

    data = response.json()

    try:
        text = data["candidates"][0]["content"]["parts"][0]["text"]
    except (KeyError, IndexError):
        raise Exception("Gemini returned an unexpected response")

    return clean_java_code(text)


def build_problem_prompt(problem, full_problem):
    title = full_problem.get("title", problem["title"])

    description = full_problem.get("description", "")

    constraints = full_problem.get("constraints", [])

    examples = full_problem.get("examples", [])

    topics = full_problem.get("topics", [])

    code_snippets = full_problem.get("code_snippets", {})

    java_code = code_snippets.get("java", "")

    examples_text = "\n".join(
        str(example.get("example_text", example))
        if isinstance(example, dict)
        else str(example)
        for example in examples
    )

    constraints_text = "\n".join(
        str(item) for item in constraints
    )

    topics_text = ", ".join(
        str(item) for item in topics
    )

    prompt = f"""
You are an expert competitive programmer.

Solve this LeetCode problem in Java.

TITLE:
{title}

DIFFICULTY:
{problem.get("difficulty", problem.get("difficulty_level", ""))}

TOPICS:
{topics_text}
