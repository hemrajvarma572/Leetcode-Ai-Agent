import json
import random
import requests


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
        timeout=120
    )

    response.raise_for_status()

    data = response.json()

    # The database may be a list
    if isinstance(data, list):
        return data

    # Or it may be wrapped inside a dictionary
    if isinstance(data, dict):

        for key in [
            "problems",
            "questions",
            "data",
            "problemsetQuestionList",
            "problemsetQuestionListV2"
        ]:

            value = data.get(key)

            if isinstance(value, list):
                return value

            if isinstance(value, dict):

                for nested_key in [
                    "questions",
                    "problems"
                ]:

                    nested = value.get(nested_key)

                    if isinstance(nested, list):
                        return nested

    raise Exception(
        "Could not find problem list in database."
    )


def select_problem():

    solved = load_solved()

    problems = download_problems()

    print(
        f"Database contains {len(problems)} problems."
    )

    available = []

    for problem in problems:

        if not isinstance(problem, dict):
            continue

        slug = problem.get(
            "problem_slug",
            problem.get(
                "titleSlug",
                problem.get("slug", "")
            )
        )

        problem_id = problem.get(
            "frontend_id",
            problem.get(
                "frontendQuestionId",
                problem.get(
                    "problem_id",
                    problem.get("id", "")
                )
            )
        )

        title = problem.get(
            "title",
            ""
        )

        difficulty = problem.get(
            "difficulty",
            ""
        )

        if not slug:
            continue

        if not problem_id:
            continue

        if slug in solved:
            continue

        available.append({
            "id": str(problem_id),
            "title": title,
            "slug": slug,
            "difficulty": difficulty
        })

    if not available:
        raise Exception(
            "No unsolved problems available!"
        )

    print(
        f"Available unsolved problems: "
        f"{len(available)}"
    )

    return random.choice(available)


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
