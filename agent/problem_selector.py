import json
import random
import requests

SOLVED_FILE = "data/solved.json"

LEETCODE_URL = "https://leetcode.com/graphql"

QUERY = """
query problemsetQuestionListV2(
    $filters: QuestionFilterInput,
    $limit: Int,
    $searchKeyword: String,
    $skip: Int,
    $sortBy: QuestionSortByInput,
    $categorySlug: String
) {
    problemsetQuestionListV2(
        filters: $filters
        limit: $limit
        searchKeyword: $searchKeyword
        skip: $skip
        sortBy: $sortBy
        categorySlug: $categorySlug
    ) {
        questions {
            id
            titleSlug
            title
            questionFrontendId
            paidOnly
            difficulty
            topicTags {
                name
                slug
            }
        }
        totalLength
        finishedLength
        hasMore
    }
}
"""


def load_solved():
    try:
        with open(SOLVED_FILE, "r") as file:
            data = json.load(file)
            return set(data.get("solved", []))
    except FileNotFoundError:
        return set()


def get_problems():
    variables = {
        "categorySlug": "",
        "skip": 0,
        "limit": 100,
        "searchKeyword": "",
        "filters": {},
        "sortBy": {}
    }

    response = requests.post(
        LEETCODE_URL,
        json={
            "query": QUERY,
            "variables": variables
        },
        headers={
            "Content-Type": "application/json",
            "User-Agent": "Mozilla/5.0"
        },
        timeout=30
    )

    response.raise_for_status()

    result = response.json()

    if "errors" in result:
        raise Exception(
            "LeetCode API error: " + str(result["errors"])
        )

    data = result.get("data")

    if not data:
        raise Exception("No data received from LeetCode")

    problem_data = data.get("problemsetQuestionListV2")

    if not problem_data:
        raise Exception("Problem list not found")

    return problem_data.get("questions", [])


def select_problem():
    solved = load_solved()
    problems = get_problems()

    available = []

    for problem in problems:

        if not isinstance(problem, dict):
            continue

        slug = problem.get("titleSlug")

        if not slug:
            continue

        # Don't select premium problems
        if problem.get("paidOnly"):
            continue

        # Don't select previously used problems
        if slug in solved:
            continue

        available.append(problem)

    if not available:
        raise Exception("No new problems available")

    return random.choice(available)


if __name__ == "__main__":

    problem = select_problem()

    print("")
    print("========================================")
    print("       TODAY'S LEETCODE PROBLEM")
    print("========================================")

    print("ID:", problem.get("questionFrontendId"))
    print("Title:", problem.get("title"))
    print("Difficulty:", problem.get("difficulty"))
    print("Slug:", problem.get("titleSlug"))

    tags = problem.get("topicTags", [])

    if tags:
        print(
            "Topics:",
            ", ".join(tag.get("name", "") for tag in tags)
        )

    print(
        "URL:",
        "https://leetcode.com/problems/"
        + problem["titleSlug"]
        + "/"
    )

    print("========================================")
