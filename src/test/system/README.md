# System Testing

"Test" is a shell file that runs a bunch of tests based on an array in the
file "TESTS". It reads zone files from the directory "zone\_files" and
compares the results with the results in the directory "named" that are
generated by bind.

If you really want to run these under Windows, see:
* Install cygwin
* Install Java and set environment: http://horstmann.com/articles/cygwin-tips.html
* Install bind-utils: http://blog.gavinadams.org/2014/05/19/dns-lookup-command-dig-under-cygwin/

At some point, the Ubuntu shell in Windows should be widely available so these
instructions won't be useful.