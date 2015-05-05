#!/usr/bin/env groovy

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import groovyx.net.http.ContentType

def createPost = { wpurl, wpusername, wppassword, title, content_raw, type, status ->
	def attributes = [title: title, content_raw: content_raw, type: type]
	def builder = new groovy.json.JsonBuilder();
	def root = builder title: title, content_raw: content_raw, type: type, status: status
	def http = new HTTPBuilder(wpurl)
	def authorization = "Basic ${"${wpusername}:${wppassword}".bytes.encodeBase64().toString()}"
	http.request(POST) {
		uri.path = '/wp-json/posts'
		requestContentType = ContentType.JSON
		body = builder.toString()
		headers.'Authorization' =  authorization

		response.success = { resp, json ->
			println "Success! ${resp.status}"
		}

		response.failure = { resp, json ->
			println "Request failed with status ${resp.status}"
			println "${json}"
		}
	}
}
	
def uploadMedia = { wpurl, wpusername, wppassword, mediaurl, mediaalt ->
	def http = new HTTPBuilder(wpurl)
	def authorization = "Basic ${"${wpusername}:${wppassword}".bytes.encodeBase64().toString()}"
	def filepath = mediaurl
	def filename = mediaalt ?: filepath.split('/').last()
	def bytes = new URL(filepath).bytes
	def contentDisposition = "attachment; filename=${filename}"

	http.request(POST) {
		uri.path = '/wp-json/media'
		body = bytes
		requestContentType = ContentType.BINARY
		headers.'Content-Disposition' = contentDisposition
		headers.'Authorization' =  authorization

		response.success = { resp, json ->
			println "Success! ${resp.status}"
			println "Uploaded ${mediaurl}"
		}

		response.failure = { resp, json ->
			println "Request failed with status ${resp.status}"
			println "${json}"
		}
	}
}

def cli = new CliBuilder(usage: 'WP-API Script [options] [args]')
cli.with {
	u 	(longOpt: 'username', args:1, argName: 'username', optionalArg: false, "Aliases for '-username'")  
	p 	(longOpt: 'password', args:1, argName: 'password', optionalArg: false, "Aliases for '-password'")  	
	w 	(longOpt: 'wpurl', args:1, argName: 'wpurl', optionalArg: false, "Aliases for '-wpurl'")  		
	i 	(longOpt: 'image', args:1, argName: 'image', optionalArg: false, "Aliases for '-image'")  		
	a 	(longOpt: 'alt', args:1, argName: 'alt', optionalArg: false, "Aliases for '-alt'")  		
	n 	(longOpt: 'name', args:1, argName: 'name', optionalArg: false, "Aliases for '-name'")  		
	t 	(longOpt: 'type', args:1, argName: 'type', optionalArg: false, "Aliases for '-type'")  			
	r 	(longOpt: 'rawcontent', args:1, argName: 'rawcontent', optionalArg: false, "Aliases for '-rawcontent'")  			
	s 	(longOpt: 'status', args:1, argName: 'status', optionalArg: false, "Aliases for '-status'")  				
	c 	(longOpt: 'cmd', args:1, argName: 'cmd', optionalArg: false, "Aliases for '-cmd'")  		
	'?'	(longOpt: 'help', 'usage information')  
	'v'	(longOpt: 'version', 'usage information')  
}

def opt = cli.parse(args)
if(!opt) { // usage already displayed by cli.parse()
	System.exit(2)	
}

if(opt.'?') {
	cli.usage()
} else if(opt.v) {
	println "Version 1.0.0"
} else {
	def username = null
	def password = null	
	def wpurl = null		
	def cmd = null		
	
	
	if(opt.u || opt.username) {
		username = opt.u ?: opt.username
	}
	if(opt.p || opt.password) {
		password = opt.p ?: opt.password
	}
	if(opt.w || opt.wpurl) {
		wpurl = opt.w ?: opt.wpurl
	}
	if(opt.c || opt.cmd) {
		cmd = opt.c ?: opt.cmd
	}
	
	if(!username || !password || !wpurl || !cmd) {
		println "missing username, password, cmd or wordpress url"
		cli.usage()
		System.exit(2)	
	}

	if(cmd == 'media') {
		def image = null
		if(opt.i || opt.image) {
			image = opt.i ?: opt.image
		}	
		def alt = null
		if(opt.a || opt.alt) {
			alt = opt.a ?: opt.alt
		}
		if(!image || !alt) {
			cli.usage()
			System.exit(2)	
		}
		uploadMedia(wpurl, username, password, image, alt)

	} else if(cmd == 'posts') {
		def type = null
		if(opt.t || opt.type) {
			type = opt.t ?: opt.type
		}
		def name = null
		if(opt.n || opt.name) {
			name = opt.t ?: opt.name
		}	
		def rawcontent = null
		if(opt.r || opt.rawcontent) {
			rawcontent = opt.r ?: opt.rawcontent
		}
		def status = null
		if(opt.s || opt.status) {
			status = opt.s ?: opt.status
		}
		if(!name || !rawcontent || !status || !type) {
			cli.usage()
			System.exit(2)	
		}
		createPost(wpurl, username, password, name, rawcontent, type, status)
	} else {
		println "wrong command ${username} ${cmd}"
		cli.usage()
	}
}	
