 function toggleContainer(idContainer, idLink, strshow, strhide, clsOpen, clsClosed, idparentContainer, parentClassNameOpen, ParentClassNameClosed)
 {
     if (document.getElementById(idContainer).style.display == "none" || document.getElementById(idContainer).style.display == "")
     {
         document.getElementById(idLink).innerHTML = strhide;
         document.getElementById(idLink).className = clsOpen;
         document.getElementById(idContainer).style.display = "block";

         document.getElementById(idparentContainer).className = ParentClassNameClosed;

     } else {
         document.getElementById(idLink).innerHTML = strshow;
         document.getElementById(idLink).className = clsClosed;
         document.getElementById(idContainer).style.display = "none";
         document.getElementById(idparentContainer).className = parentClassNameOpen;
     }
 }
