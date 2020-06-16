markdown-pdf .\pid.md .\logboek.md -s .\github.css -o documentatie.pdf

git add .
git commit -m $args[0]
git push origin master