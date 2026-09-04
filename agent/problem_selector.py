import json
import random
import requests

SOLVED_FILE = "data/solved.json"

# Public LeetCode problem list
URL = "https://raw.githubusercontent.com/CodepediaOrg/codepedia/master/leetcode.json"


def load_solved():
    try:
        with open(SOLVED_FILE, "r") as f:
            data = json.load(f)
            return set(data.get("solved", []))
    except FileNotFoundError:
        return set()


def get_problems():
    response = requests.get(URL, timeout=30)
    response.raise_for_status()

    data = response.json()

    if not isinstance(data, list):
        raise ValueError("Problem database is not a list")

    problems = []

    for p in data:
        if not isinstance(p, dict):
            continue

        title = p.get("title")
        slug = p.get("slug")

        if title and slug:
            problems.append({
                "title": title,
                "slug": slug,
                "url": f"https://leetcode.com/problems/{slug}/"
            })

    return problems


def select_problem():
    solved = load_solved()
    problems = get_problems()

    available = [
        p for p in problems
        if p["slug"] not in solved
    ]

    if not available:
        raise Exception("No new problems available")

    return random.choice(available)


if __name__ == "__main__":
    problem = select_problem()

    print("================================")
    print("TODAY'S LEETCODE PROBLEM")
    print("================================")
    print("Title:", problem["title"])
    print("Slug:", problem["slug"])
    print("URL:", problem["url"])
    print("================================")
