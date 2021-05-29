osascript -e 'tell app "Terminal"
    do script "./ngrok tcp 5108 --region eu"
end tell'
