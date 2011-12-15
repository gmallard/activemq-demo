#
require 'rubygems'
require 'stomp'
#
# Author: Guy Allard
#
c = Stomp::Connection.open('my', 'mypw', 'localhost', 61613) # AMQ is here
c.subscribe "/queue/GMA.Q01", :ack => :client
1.upto(8) do
  msg = c.receive
  p [ "dbg01",  msg ]
  c.ack msg.headers['message-id']
end
c.unsubscribe "/queue/GMA.Q01"
c.disconnect


