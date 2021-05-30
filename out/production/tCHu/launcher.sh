#!/bin/bash
echo "ngrok launching is on progress..."

# shellcheck disable=SC2016
osascript -e 'tell app "Terminal"
    do script "./ngrok tcp '$1' --region eu"
end tell'
