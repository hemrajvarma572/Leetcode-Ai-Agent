import json
import random
import requests
import os


SOLVED_FILE = "data/solved.json"

DATABASE_URL = (
    "https://raw.githubusercontent.com/"
    "mcaupybugs/leetcode-problems-db/master/"
    "merged_problems.json"
)


def load_solved():
    try:
        with open(SOLVED_FILE, "r") as file:
            data = json.load(file)

        return set(data.get("solved", []))

    except FileNotFoundError:
        return set()


def download_problems():

    print("Downloading LeetCode problem database...")

    response = requests.get(
        DATABASE_URL,
        timeout=60
    )

    response.raise_for_status()

    data = response.json()

    if not isinstance(data, list):
        raise Exception(
            "Unexpected database format."
        )

    print(
        f"Database loaded: {len(data)} problems"
    )

    return data


def select_problem():

    solved = load_solved()

    problems = download_problems()

    available = []

    for problem in problems:

        slug = problem.get(
            "problem_slug",
            ""
        )

        problem_id = problem.get(
            "frontend_id",
            problem.get("problem_id", "")
        )

        if not slug:
            continue

        if slug in solved:
            continue

        available.append({
            "id": str(problem_id),
            "title": problem.get(
                "title",
                ""
            ),
            "slug": slug,
            "difficulty": problem.get(
                "difficulty",
                ""
            )
        })

    if not available:
        raise Exception(
            "No unsolved problems available!"
        )

    problem = random.choice(
        available
    )

    print(
        f"Available unsolved problems: "
        f"{len(available)}"
    )

    return problem


if __name__ == "__main__":

    problem = select_problem()

    print("")
    print("========================================")
    print("       TODAY'S LEETCODE PROBLEM")
    print("========================================")
    print("ID:", problem["id"])
    print("Title:", problem["title"])
    print("Difficulty:", problem["difficulty"])
    print("Slug:", problem["slug"])
    print(
        "URL:",
        f"https://leetcode.com/problems/"
        f"{problem['slug']}/"
    )
    print("========================================")
