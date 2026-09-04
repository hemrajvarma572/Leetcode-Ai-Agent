import json
import random

SOLVED_FILE = "data/solved.json"

PROBLEMS = [
    {
        "id": "1",
        "title": "Two Sum",
        "slug": "two-sum",
        "difficulty": "Easy"
    },
    {
        "id": "20",
        "title": "Valid Parentheses",
        "slug": "valid-parentheses",
        "difficulty": "Easy"
    },
    {
        "id": "121",
        "title": "Best Time to Buy and Sell Stock",
        "slug": "best-time-to-buy-and-sell-stock",
        "difficulty": "Easy"
    },
    {
        "id": "125",
        "title": "Valid Palindrome",
        "slug": "valid-palindrome",
        "difficulty": "Easy"
    },
    {
        "id": "206",
        "title": "Reverse Linked List",
        "slug": "reverse-linked-list",
        "difficulty": "Easy"
    },
    {
        "id": "704",
        "title": "Binary Search",
        "slug": "binary-search",
        "difficulty": "Easy"
    },
    {
        "id": "217",
        "title": "Contains Duplicate",
        "slug": "contains-duplicate",
        "difficulty": "Easy"
    },
    {
        "id": "53",
        "title": "Maximum Subarray",
        "slug": "maximum-subarray",
        "difficulty": "Medium"
    },
    {
        "id": "15",
        "title": "3Sum",
        "slug": "3sum",
        "difficulty": "Medium"
    },
    {
        "id": "49",
        "title": "Group Anagrams",
        "slug": "group-anagrams",
        "difficulty": "Medium"
    }
]


def load_solved():
    try:
        with open(SOLVED_FILE, "r") as file:
            data = json.load(file)
            return set(data.get("solved", []))
    except FileNotFoundError:
        return set()


def select_problem():
    solved = load_solved()

    available = [
        problem
        for problem in PROBLEMS
        if problem["slug"] not in solved
    ]

    if not available:
        raise Exception("No new problems available!")

    return random.choice(available)


if __name__ == "__main__":
    problem = select_problem()

    print("========================================")
    print("       TODAY'S LEETCODE PROBLEM")
    print("========================================")
    print("ID:", problem["id"])
    print("Title:", problem["title"])
    print("Difficulty:", problem["difficulty"])
    print("Slug:", problem["slug"])
    print(
        "URL:",
        f"https://leetcode.com/problems/{problem['slug']}/"
    )
    print("========================================")
