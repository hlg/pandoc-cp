:let oldheader= search("^% ")
:while oldheader > 0
  :echo "dropping old header:"
  :echo getbufline('',oldheader) 
  :execute 'normal '.oldheader.'gg'
  :normal dd
  :let oldheader= search("^% ")
:endwhile
:%s/^\(.*\)\n=\+$/% \1/ge
:%s/^\(.*\)\n[-]\+$/\1\r===========================================================================/ge
:%s/^### \(.*\)$/\1\r-------------------------------------------------------------------/ge
:%s/^#### /### /ge
:%s/^##### /####/ge
:%s/^###### /#####/ge
:w
:q

