import os
import json
import re
import requests

from problem_selector import select_problem

GEMINI_URL = (
    "https://generativelanguage.googleapis.com/v1beta/"
    "models/gemini-2.5-flash:generateContent"
)

SOLVED_FILE = "data/solved.json"


def ask_gemini(problem):
    api_key = os.environ.get("GEMINI_API_KEY")

    if not api_key:
        raise Exception("GEMINI_API_KEY is not configured")


    prompt = f"""
You are an expert competitive programmer.

Solve this LeetCode problem in Java.

Problem:
{problem["title"]}

LeetCode URL:
https://leetcode.com/problems/{problem["slug"]}/

Requirements:
1. Give a correct LeetCode submission.
2. Use Java.
3. Include the required Solution class.
4. Do NOT include a main method.
5. Do NOT use markdown code fences.
6. Return ONLY the complete Java code.
7. Prefer an efficient time and space complexity.
"""

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

    # Remove markdown fences if Gemini accidentally adds them
    text = re.sub(r"```java", "", text, flags=re.IGNORECASE)
    text = re.sub(r"```", "", text)

    return text.strip()


def mark_solved(slug):
    with open(SOLVED_FILE, "r") as file:
        data = json.load(file)

    if slug not in data["solved"]:
        data["solved"].append(slug)

    with open(SOLVED_FILE, "w") as file:
        json.dump(data, file, indent=2)


def save_solution(problem, code):
    os.makedirs("solutions", exist_ok=True)

    filename = f"{problem['id']}-{problem['slug']}.java"
    filepath = os.path.join("solutions", filename)

    with open(filepath, "w") as file:
        file.write(code)

    return filepath


def main():
    problem = select_problem()

    print("Selected problem:")
    print(problem["title"])
    print(problem["difficulty"])

    print("\nAsking Gemini to solve...")

    code = ask_gemini(problem)

    if len(code) < 50:
        raise Exception("Generated code looks invalid")

    filepath = save_solution(problem, code)

    # Mark only after a solution was generated successfully
    mark_solved(problem["slug"])

    print("\nSUCCESS!")
    print("Problem:", problem["title"])
    print("Solution:", filepath)


if __name__ == "__main__":
    main()
