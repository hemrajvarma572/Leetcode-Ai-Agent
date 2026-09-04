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
    response.raise_for_status()

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

Solve this LeetCode problem in Java.

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

IMPORTANT:

The starter code may use different class names.

Examples include:
Solution
Codec
and other required class names.

You MUST preserve the exact required public
class name and method signature.

LeetCode may provide data structures such as:
TreeNode
ListNode
Node

Do not unnecessarily redefine these structures
inside the submitted solution.

REQUIREMENTS:

1. Return a correct LeetCode submission.
2. Use Java.
3. Preserve the required class name.
4. Preserve the required method signatures.
5. Do NOT include a main method.
6. Include necessary imports.
7. Use an efficient algorithm.
8. Handle edge cases.
9. Return ONLY Java code.
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


def get_public_class_name(code):
    match = re.search(
        r"public\s+class\s+([A-Za-z_][A-Za-z0-9_]*)",
        code
    )

    if match:
        return match.group(1)

    match = re.search(
        r"class\s+([A-Za-z_][A-Za-z0-9_]*)",
        code
    )

    if match:
        return match.group(1)

    return "Solution"


def create_support_files(code, temp_dir):
    if re.search(r"\bTreeNode\b", code):
        tree_node = """
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
"""

        with open(
            os.path.join(temp_dir, "TreeNode.java"),
            "w"
        ) as file:
            file.write(tree_node)

    if re.search(r"\bListNode\b", code):
        list_node = """
class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
"""

        with open(
            os.path.join(temp_dir, "ListNode.java"),
            "w"
        ) as file:
            file.write(list_node)

    if re.search(r"\bNode\b", code):
        node = """
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;
    public java.util.List<Node> children;

    public Node() {}

    public Node(int val) {
        this.val = val;
    }

    public Node(int val, Node left, Node right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
"""

        with open(
            os.path.join(temp_dir, "Node.java"),
            "w"
        ) as file:
            file.write(node)


def compile_java(code):
    temp_dir = tempfile.mkdtemp()

    try:
        class_name = get_public_class_name(code)

        java_file = os.path.join(
            temp_dir,
            f"{class_name}.java"
        )

        with open(java_file, "w") as file:
            file.write(code)

        create_support_files(
            code,
            temp_dir
        )

        java_files = [
            os.path.join(temp_dir, filename)
            for filename in os.listdir(temp_dir)
            if filename.endswith(".java")
        ]

        result = subprocess.run(
            ["javac"] + java_files,
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

    java_code = (
        full_problem
        .get("code_snippets", {})
        .get("java", "")
    )

    prompt = f'''
You are an expert Java competitive programmer.

A Java solution for this LeetCode problem failed
local compilation.

PROBLEM:
{title}

PROBLEM DESCRIPTION:
{description}

JAVA STARTER CODE:
{java_code}

CURRENT JAVA CODE:
{code}

COMPILATION ERROR:
{error}

Fix the Java solution.

IMPORTANT:

Preserve the exact class name and method signature
required by the LeetCode starter code.

LeetCode may provide:
TreeNode
ListNode
Node

These support classes are supplied by our local
compiler for testing. Do not unnecessarily add
duplicate definitions to the submitted solution.

REQUIREMENTS:

1. Fix the compilation error.
2. Keep the original algorithm correct.
3. Preserve the required class name.
4. Preserve the required method signatures.
5. Include necessary imports.
6. Do NOT add a main method.
7. Return ONLY complete Java code.
8. Do NOT use markdown code fences.
'''

    print("Asking Gemini to fix the code...")

    return call_gemini(prompt)


def test_and_fix(problem, full_problem, code):
    for attempt in range(1, MAX_FIX_ATTEMPTS + 1):
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
                "Gemini returned invalid-looking Java code."
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

    full_problem = get_full_problem(problem)

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
