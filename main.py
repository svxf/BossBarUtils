import json
import re

def parse_compound(compound):
    """Parse the compound into a JSON-compatible list."""
    result = []
    pattern = re.compile(r"literal\{([^}]*)\}\[style=\{([^}]*)\}\]")
    matches = re.findall(pattern, compound)

    for text, style in matches:
        entry = {"text": text}  # Keep text as it is
        styles = style.split(",")
        for style_item in styles:
            key, value = style_item.split("=")
            key, value = key.strip(), value.strip()
            if key == "color":
                entry["color"] = value
            elif key == "hoverEvent":
                entry["hoverEvent"] = {
                    "action": "show_text",
                    "contents": [{"text": "Media", "color": "#CC39E9"}]
                }
        result.append(entry)
    return result

def generate_bossbar_command(bossbar_id, compound):
    """Generate the /bossbar set command."""
    try:
        parsed_compound = parse_compound(compound)
        # Using ensure_ascii=False to maintain Unicode characters
        command = f'/bossbar set {bossbar_id} name {json.dumps(parsed_compound, ensure_ascii=False)}'
        return command
    except Exception as e:
        return f"Error processing compound: {str(e)}"

if __name__ == "__main__":
    bossbar_id = input("Enter the bossbar ID: ")
    compound_input = input("Paste the compound text: ")
    command = generate_bossbar_command(bossbar_id, compound_input)
    print("\nGenerated Command:")
    print(command)