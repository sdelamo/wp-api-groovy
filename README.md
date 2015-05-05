# wp-api-groovy
Groovy Script to interact with a Wordpress JSON Api provided by WP-API 
# Prerequisites

You would need to have installed in your Wordpress press site the following plugins: 

- [WP Rest Api Plugin](http://wp-api.org)

In order to use the 'media' and 'posts' commands which require authentication you would need: 

- [Basic-Auth Plugin](https://github.com/WP-API/Basic-Auth)


## Usage


### Create Post

    $ ./wp-api.groovy -c posts -u myusername -p mypassword -s published -t post -n Ou -r Yeah -w http://mywordpresssite.com
    
### Upload Image

    $ ./wp-api.groovy -c media -u myusername -p mypassword -w http://mywordpresssite.com -i http://upload.wikimedia.org/wikipedia/commons/f/ff/Wikipedia_logo_593.jpg -a wikipedia

