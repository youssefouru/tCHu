#!/bin/bash
if [ "$1" = "Mac" ]; then
    osascript -e 'tell app "Terminal"
    do script "./ngrok tcp '$2' --region eu"
  end tell'
  elif [ "$1" = "Linux" ]
  then
    konsole -e "./ngrok tcp '$2' --region eu"
  else
    echo "on ne peut pas effectuer cette opération sur un autre systeme d'exploitation autre que linux et mac"
fi