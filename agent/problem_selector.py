import json
import random
import requests

PROBLEMS_URL = "https://raw.githubusercontent.com/karan/LeetCode/master/README.md"
SOLVED_FILE = "data/solved.json"


def load_solved():
    with open(SOLVED_FILE, "r") as file:
        data = json.load(file)

    return set(data["solved"])


def save_solved(problem_slug):
    with open(SOLVED_FILE, "r") as file:
        data = json.load(file)

    data["solved"].append(problem_slug)

    with open(SOLVED_FILE, "w") as file:
        json.dump(data, file, indent=2)


def get_problems():
    response = requests.get(PROBLEMS_URL)
    response.raise_for_status()

    problems = []

    for line in response.text.splitlines():
        if "leetcode.com/problems/" in line:
            parts = line.split("leetcode.com/problems/")

            if len(parts) > 1:
                slug = parts[1].split("/")[0]

                if slug and slug not in problems:
                    problems.append(slug)

    return problems


def select_problem():
    solved = load_solved()
    problems = get_problems()

    available = [
        problem for problem in problems
        if problem not in solved
    ]

    if not available:
        raise Exception("No new problems available!")

    return random.choice(available)


if __name__ == "__main__":
    problem = select_problem()

    print("Today's LeetCode problem:")
    print(problem)

    save_solved(problem)
