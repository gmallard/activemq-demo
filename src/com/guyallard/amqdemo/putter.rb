require 'rubygems' 
require 'stomp'
#
# Author: Guy Allard
#
c = Stomp::Connection.open('my', 'mypw', 'localhost', 61613) # AMQ is here 
#
1.upto(10) do
  # The suppress_content_length => true header is to make sure the JMS message is a Text message.
  # See comments in the stomp gem's source for further details.
  c.publish "/queue/GMA.Q01", "a simple message: #{Time.now.to_f}", {"suppress_content_length" => "true"}
end
#
c.disconnect
  
