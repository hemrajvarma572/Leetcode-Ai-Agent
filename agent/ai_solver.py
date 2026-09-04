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
            data = json.load(file)
            return set(data.get("solved", []))
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
    title = full_problem.get(
        "title",
        problem["title"]
    )

    description = full_problem.get(
        "description",
        ""
    )

    constraints = full_problem.get(
        "constraints",
        []
    )

    examples = full_problem.get(
        "examples",
        []
    )

    topics = full_problem.get(
        "topics",
        []
    )

    code_snippets = full_problem.get(
        "code_snippets",
        {}
    )

    java_code = code_snippets.get(
        "java",
        ""
    )

    examples_text = "\n".join(
        str(example.get("example_text", example))
        if isinstance(example, dict)
        else str(example)
        for example in examples
    )

    constraints_text = "\n".join(
        str(item)
        for item in constraints
    )

    topics_text = ", ".join(
        str(item)
        for item in topics
    )

    difficulty = full_problem.get(
        "difficulty",
        problem.get("difficulty", "")
    )

    prompt = f'''
You are an expert competitive programmer.

Solve the following LeetCode problem in Java.

TITLE:
{title}

DIFFICULTY:
{difficulty}

TOPICS:
{topics_text}

PROBLEM DESCRIPTION:
{description}

EXAMPLES:
{examples_text}

CONSTRAINTS:
{constraints_text}

JAVA STARTER CODE:
{java_code}

REQUIREMENTS:

1. Return a correct LeetCode submission.
2. Use Java.
3. Preserve the required Solution class.
4. Preserve the required method signature from the starter code.
5. Do NOT include a main method.
6. Use an efficient algorithm appropriate for the constraints.
7. Carefully handle edge cases.
8. Include all necessary Java imports.
9. Return ONLY the complete Java code.
10. Do NOT use markdown code fences.
'''

    return prompt


def generate_solution(problem, full_problem):
    prompt = build_problem_prompt(
        problem,
        full_problem
    )

    print("Asking Gemini to generate solution...")

    return call_gemini(prompt)


def compile_java(code):
    temp_dir = tempfile.mkdtemp()

    try:
        java_file = os.path.join(
            temp_dir,
            "Solution.java"
        )

        with open(java_file, "w") as file:
            file.write(code)

        result = subprocess.run(
            ["javac", java_file],
            capture_output=True,
            text=True,
            timeout=30
        )

        if result.returncode == 0:
            return True, ""

        return False, result.stderr.strip()

    except subprocess.TimeoutExpired:
        return False, "Java compilation timed out."

    finally:
        shutil.rmtree(
            temp_dir,
            ignore_errors=True
        )


def fix_solution(problem, full_problem, code, error):
    title = full_problem.get(
        "title",
        problem["title"]
    )

    description = full_problem.get(
        "description",
        ""
    )

    prompt = f'''
You are an expert Java competitive programmer.

A Java solution for the following LeetCode problem failed to compile.

PROBLEM:
{title}

PROBLEM DESCRIPTION:
{description}

CURRENT JAVA CODE:
{code}

COMPILATION ERROR:
{error}

Fix the Java code.

IMPORTANT REQUIREMENTS:

1. Fix the compilation error.
2. Keep the solution correct for the original problem.
3. Preserve the required Solution class.
4. Preserve the required LeetCode method signature.
5. Include all necessary Java imports.
6. Do NOT add a main method.
7. Do NOT explain anything.
8. Return ONLY the complete corrected Java code.
9. Do NOT use markdown code fences.
'''

    print("Asking Gemini to fix the code...")

    return call_gemini(prompt)


def test_and_fix(problem, full_problem, code):
    for attempt in range(
        1,
        MAX_FIX_ATTEMPTS + 1
    ):

        print("")
        print("----------------------------------------")
        print(
            f"JAVA TEST ATTEMPT "
            f"{attempt}/{MAX_FIX_ATTEMPTS}"
        )
        print("----------------------------------------")

        success, error = compile_java(code)

        if success:
            print("Java compilation: SUCCESS")
            return code

        print("Java compilation: FAILED")
        print(error)

        if attempt == MAX_FIX_ATTEMPTS:
            raise Exception(
                "Java code still fails compilation "
                f"after {MAX_FIX_ATTEMPTS} attempts."
            )

        code = fix_solution(
            problem,
            full_problem,
            code,
            error
        )

        if len(code) < 50:
            raise Exception(
                "Gemini returned code that looks invalid."
            )

    raise Exception("Unexpected testing failure.")


def save_solution(problem, code):
    os.makedirs(
        "solutions",
        exist_ok=True
    )

    filename = (
        f"{problem['id']}-"
        f"{problem['slug']}.java"
    )

    filepath = os.path.join(
        "solutions",
        filename
    )

    with open(filepath, "w") as file:
        file.write(code)

    return filepath


def main():
    problem = select_problem()

    print("")
    print("========================================")
    print("SELECTED PROBLEM")
    print("========================================")
    print("ID:", problem["id"])
    print("Title:", problem["title"])
    print("Difficulty:", problem["difficulty"])
    print("Slug:", problem["slug"])
    print("========================================")

    full_problem = get_full_problem(
        problem
    )

    print("")
    print("Full problem data loaded.")

    code = generate_solution(
        problem,
        full_problem
    )

    if len(code) < 50:
        raise Exception(
            "Generated Java code looks invalid."
        )

    code = test_and_fix(
        problem,
        full_problem,
        code
    )

    filepath = save_solution(
        problem,
        code
    )

    save_solved(
        problem["slug"]
    )

    print("")
    print("========================================")
    print("SUCCESS")
    print("========================================")
    print("Problem:", problem["title"])
    print("Java file:", filepath)
    print("Java compilation: PASSED")
    print("Saved to GitHub.")
    print("========================================")


if __name__ == "__main__":
    main()
