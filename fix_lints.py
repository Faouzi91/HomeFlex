import os
import re
from glob import glob

def fix_files(directory):
    for root, _, files in os.walk(directory):
        for file in files:
            if not file.endswith('.dart'):
                continue
            
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            new_content = content
            
            # Fix use_build_context_synchronously by replacing !mounted with !context.mounted
            new_content = re.sub(r'\bif\s*\(!mounted\)', 'if (!context.mounted)', new_content)
            
            # Fix withOpacity
            # match .withOpacity(x)
            new_content = re.sub(r'\.withOpacity\(([^)]+)\)', r'.withValues(alpha: \1)', new_content)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed {filepath}")

if __name__ == '__main__':
    fix_files('rental-app-flutter/lib')
