require "sinatra" 
require "data_mapper"
#require "pry"

# As of now, DataMapper is not used for anything. These are just placeholders.
configure :development do
	DataMapper.setup(:default, ENV['DATABASE_URL'] || "postgres://localhost/steno")
	DataMapper.auto_migrate! # wipes everything when the server is restarted
end 

configure :production do
	require 'newrelic_rpm'
	DataMapper.setup(:default, ENV['DATABASE_URL'] || "postgres://localhost/steno")
	DataMapper.finalize
	DataMapper.auto_upgrade! # Attempts to nicely consolidate Class changes
end

set :views, settings.root + '/views'

get "/" do
	@title = "Steno"
	set :erb, :layout => false
	erb :index
end

# API spec has been outlined here http://www.stypi.com/tedlee/Steno/api_spec.json


not_found do  
	halt 404, "No page for you."
end