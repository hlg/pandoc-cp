% Erstellung von Hypertexten als IMS Content Packages mit Unix-Bordmitteln
% Helga Tauscher

Mit IMS Content Packages können Lerninhalte plattformunabhängig aufbereitet und in ein LMS importiert werden. Zur Erstellung von CPs sind spezielle Authoring-Tools nötig. OLAT selbst kann z.B. dafür genutzt werden, indem ein Kursbaustein "CP" direkt in OLAT editiert wird. Auch OLAT-Kursbausteine vom Typ "Wiki" können als CP exportiert werden, dabei sind jedoch die Möglichkeiten zum Aufbau einer Struktur beschränkter. In beiden Fällen besteht eine Bindung an OLAT als Autorenumgebung.

Es wäre jedoch vorteilhaft, die Inhalte nicht nur in einem plattformunabhängigen Format austauschen zu können, sondern sie bereits auch plattformunabhängig, mit möglichst freier Wahl der Werkzeuge erstellen zu können und dabei auch möglichst große Freiheit in der Weiterverwendung der Inhalte für andere Formate und Distributionswege neben einem LMS zu haben, z.B. als gedrucktes Skript oder als Webseite. Dieses Handout will einen Weg vorstellen, mit dem dieses Ziel erreicht werden kann.

Erstellung der Inhalte
--------------------------

In den letzten Jahren sind verschiedene leichtgewichtige Auszeichnungssprachen populär geworden, um technische Dokumentationen, Inhalte für Forenbeiträge und Kommentare, aber auch persönliche Journale und anderes auf Basis von Plaintext zu erstellen. Zwei bekannte Beispiele sind die Wikisyntax und Markdown. Mit Pandoc, einem universellen und quelloffenen Dokumentenkonverter, der zahlreiche Ein- und Ausgabeformate und u.a. auch Zitationen beherrscht, hat Markdown sich aus der Nische eher technischer Communities auch im akademischen Umfeld verbreitet. Von Vorteil sind die niedrigen Einschränkungen bei der Software zum Bearbeiten (jeder Texteditor reicht aus), die Möglichkeit Versionskontrolle einzusetzen, die einfache Zugänglichkeit von Textdokumenten für die automatisierte Verarbeitung mit Skripten und die hohe Flexibilität bei den Endformaten. Was liegt also näher, als Markdown auch für die Erstellung von Lernhalten zu verwenden?

Die Inhalte werden in Kapiteln strukturiert, die vom Umfang her etwa einer 90 Minuten langen Vorlesung entsprechen. Jedes Kapitel wiederum enthält vier bis sechs Unterkapitel, die als entsprechend kürzere Einheiten in einem Lehrkonzept verwendet werden können. Die Unterkapitel können mit zwei weiteren Ebenen strukturiert werden. Für die Struktur werden die ersten vier Überschriften-Level des Pandoc Markdowns verwendet:

    Kapitel 1
    =========

    Unterkapitel 1
    --------------
    ### Abschnitt 1
    #### Unterabschnitt 1
    #### Unterabschnitt 2
    ### Abschnitt 2
    ### Abschnitt 3

    Unterkapitel 2
    --------------

    Kapitel 2
    =========

    Kapitel 3
    =========

Zum Einbinden von Grafiken oder Videos, für Links, Zitate, Listen, Quellcodes und für Literaturreferenzen kommen die entsprechenden Auszeichnungen zum Einsatz:

    Die folgende Abbildung zeigt das Schema nach @Mueller2013. Details
    finden sich auf der Webseite des [Instituts](http://www.dasinstitut.de).

    ![Bildunterschrift](img/dateiname.jpg)

    Nach @Mueller2013 hat ein XYZ folgende charakteristischen Eigenschaften:

    * rot
    * laut
    * schnell

    @Mueller2013 beschreibt das so:

    > Ein XYZ ist rot, laut und schnell.

Die mit Markdown ausgezeichneten Textdateien sind einfach zu lesen, zu durchsuchen und zu editieren. Sie erlauben es, sich auf die Inhalte zu konzentrieren und nicht durch die Formatierungen abgelenkt zu werden. Weitere Details zum pandoc finden sich im [Pandoc User Guide](http://pandoc.org/README.html).


Struktur und Aufbau eines CP
------------------------------

Die so erstellten Inhalte sollen in ein hierarchisch strukturiertes CP mit Hypertext konvertiert werden. Ein solches CP besteht aus einem zip-Archiv, das alle erforderlichen Dateien enthält und daneben eine XML-Datei mit Metadaten, der Bündelung der Dateien zu Ressourcen und Angaben zur Reihenfolge und Strukturierung der Ressourcen. Die vorliegende Konvertierung beschränkt sich auf Ressourcen vom Typ `webcontent` und eine hierarchische Struktur. Eine Struktur wie die oben in Markdown ausgedrückte soll in folgendem XML resultieren:

    <?xml version='1.0' encoding='UTF-8'?>
    <manifest identifier='cpID' schemaLocation='...'>
      <metadata />
      <organizations>
	<organization identifier='root' structure='hierarchical'>
	  <title>CP Title</title>
	  <item identifier='01-00' identifierref='r01-00' isvisible='true'>
	    <title>Kapitel 1</title>
	    <item identifier='01-01' identifierref='r01-01' isvisible='true'>
	      <title>Unterkapitel 1</title>
	    </item>
	    <item identifier='01-02' identifierref='r01-02' isvisible='true'>
	      <title>Unterkapitel 2</title>
	    </item>
	    <item identifier='01-03' identifierref='r01-03' isvisible='true'>
	      <title>Unterkapitel 3</title>
	    </item>
	  </item>
	  <item identifier='02-00' identifierref='r02-00' isvisible='true'>
	    <title>Kapitel 2</title>
	  </item>
	  <item identifier='03-00' identifierref='r03-00' isvisible='true'>
	    <title>Kapitel 3</title>
	  </item>
	</organization>
      </organizations>
      <resources>
	<resource identifier='r01-01' type='webcontent' href='unterkapitel_01-01.html'>
	  <file href='unterkapitel_01-01.html' />
	</resource>
	<resource identifier='r01-02' type='webcontent' href='unterkapitel_01-02.html'>
	  <file href='unterkapitel_01-02.html' />
	</resource>
	<resource identifier='r01-03' type='webcontent' href='unterkapitel_01-03.html'>
	  <file href='unterkapitel_01-03.html' />
	</resource>
      </resources>
    </manifest>

Die Ressourcen enthalten jeweils Referenzen auf alle notwendigen Dateien, also auch Stylesheets und Bilddateien. Die Abschnitte und Unterabschnitte sind in den HTML-Dateien als Überschriften erster und zweiter Ordnung enthalten (`h1` und `h2`). 


Konvertierung in ein CP
=============================

Um die Markdown-Datei in ein standardkonformes CP zu konvertieren, wurden Shell- und Groovy-Skripte eingesetzt und ein makefile zusammengestellt. Die Skripte leisten die folgenden Konvertierungsschritte:

* Aufsplitten der ursprünglichen Markdown-Datei in Einzeldateien auf der Überschriftenebene 3, Anpassen der Überschriftenlevel in den geteilten Dateien
* Konvertierung der Teildateien in html-Dateien mit Pandoc
* Erstellen eines IMS-Manifests mit der Struktur und den Titeln der Kapitel und Unterkapitel
* Zusammenfügen der Einzeldateien in einem zip-Archiv

Die Skripte sind auf Github verfügbar: [http://github.com/hlg/pandoc-cp](http://github.com/hlg/pandoc-cp) Für die Nutzung der Skripte müssen folgende Softwarepakete installiert sein: Pandoc, Groovy, Make, Vim.

Die Skripte können als zip-Datei heruntergeladen und entpackt werden. Die Default-CSS-Datei styles.css wird ins CP eingeschlossen und kann nach Belieben angepasst werden. Die Titel der Kapitel müssen vorerst noch in eine separate Datei `lessons.txt` kopiert werden, um in das Manifest korrekt aufgenommen zu werden. Jedes Kapitel soll auf einer separaten Zeile betitelt werden, ohne Leerzeilen am Ende der Datei. Die Konvertierung einer Datei `script.md` in ein CP erfolgt dann mit folgenden Schritten:

    make script
    make script.zip
    make clean

Mit `make script` wird das Skript in Unterkapitel aufgetrennt, `make script.zip` konvertiert in html Dateien und erstellt das CP inklusive Manifest und `make clean` schließlich bereinigt temporäre Dateien aus dem Konvertierungsprozess. Der Dateiname der Markdowndatei sollte nicht der Form `lesson_*.md` entsprechen, da er sonst beim Aufräumen gelöscht wird.












 
