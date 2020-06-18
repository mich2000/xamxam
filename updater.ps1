Set-Location .\PID
.\updater.ps1
Set-Location ..

git add .
git commit -m $args[0]
git push origin master