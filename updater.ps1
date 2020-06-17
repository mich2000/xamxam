markdown-pdf .\PID\documentatie.md -s .\PID\github.css -o \PID\documentatie.pdf

git add .
git commit -m $args[0]
git push origin master