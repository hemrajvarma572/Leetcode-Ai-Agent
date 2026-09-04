import json
import random
import requests

PROBLEMS_URL = (
    "https://raw.githubusercontent.com/"
    "mcaupybugs/leetcode-problems-db/master/merged_problems.json"
)

SOLVED_FILE = "data/solved.json"


def load_solved():
    with open(SOLVED_FILE, "r") as file:
        return set(json.load(file)["solved"])


def save_solved(slug):
    with open(SOLVED_FILE, "r") as file:
        data = json.load(file)

    data["solved"].append(slug)

    with open(SOLVED_FILE, "w") as file:
        json.dump(data, file, indent=2)


def get_problems():
    response = requests.get(PROBLEMS_URL, timeout=30)
    response.raise_for_status()

    return response.json()


def select_problem():
    solved = load_solved()
    problems = get_problems()

    available = [
        p for p in problems
        if p.get("problem_slug") not in solved
        and not p.get("isPaidOnly", False)
    ]

    if not available:
        raise Exception("No new problems available!")

    return random.choice(available)


if __name__ == "__main__":
    problem = select_problem()

    print("Today's LeetCode problem:")
    print("ID:", problem.get("frontend_id"))
    print("Title:", problem.get("title"))
    print("Difficulty:", problem.get("difficulty"))
    print("Slug:", problem.get("problem_slug"))

    save_solved(problem.get("problem_slug"))
