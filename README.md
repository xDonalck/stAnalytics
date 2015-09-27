# stAnalytics
View daily unique players and playercount

stAnalytics tracks who logs into your server every day, as well as gets the player count for your server every 15 minutes. It saves all of this in a MySQL database.

Currently there is no front-end for this, so you'd have to view the data through phpmyadmin or another similar database viewer, however I have planned a web interface to view the data in graphs and the like, and will release it soon.

This currently doesn't collect much data and took around 1-2 hours to make, I'll be increasing what you can track and the frequencies of it, if you have a suggestion I'd be happy to consider adding it.

stAnalytics was created as an alternative to MineTrends, as I really couldn't justify paying a few bucks a month for something as simple as this.

Commands
/sta reload

Permissions
stAnalytics.*
stAnalytics.reload (Use /sta reload)
