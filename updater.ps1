cd .\PID
.\PID\updater.ps1
cd ..

git add .
git commit -m $args[0]
git push origin master