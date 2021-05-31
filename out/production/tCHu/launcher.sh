#!/bin/bash
osascript -e 'tell app "Terminal"
    do script "./ngrok tcp '$1' --region eu"
end tell'
