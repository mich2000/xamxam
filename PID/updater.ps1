markdown-pdf .\documentatie.md -s .\github.css

git add .
git commit -m $args[0]
git push origin master