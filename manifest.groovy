new File("imsmanifest.xml").withWriter { writer ->
  def lessons = new File("lessons.txt").readLines()
  def xml = new groovy.xml.MarkupBuilder(writer)
  def helper = new groovy.xml.MarkupBuilderHelper(xml)
  helper.xmlDeclaration([version:'1.0', encoding:'UTF-8'])
  def currentDir = new File('.')
  xml.manifest(identifier:"bim-theory",schemaLocation:"http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd http://www.imsglobal.org/xsd/imsmd_v1p2 imsmd_v1p2p2.xsd"){
    metadata() // TODO
    organizations {
      organization(identifier:"root",structure:"hierarchical"){
	title("Building Information Modelling")
	lessons.eachWithIndex { lessonTitle, lessonId ->
	item(identifier:'0'+(lessonId+1)+'-00', identifierref:'r0'+(lessonId+1)+'-00', isvisible:'true'){
	  title( lessonTitle )
	  def chapterFiles = []  // use listFiles with Filefilter to sort 
	  currentDir.eachFileMatch(~/lesson_0${lessonId+1}-\d\d.md/){ chapterFile -> chapterFiles << chapterFile }
	  chapterFiles.sort{ it.name }.each{ chapterFile ->
	    def chapterId = chapterFile.name[7..11]
	    if(chapterId[3..4]!='00') item(identifier:chapterId, identifierref:'r'+chapterId, isvisible:'true'){
	      title( chapterFile.withReader{ r -> r.readLine()[2..-1] } )
	    }
	  }
	}}
      }
    }
    resources {
      def htmlFiles = []
      currentDir.eachFileMatch(~/lesson_.*\.html/){ htmlFile -> 
	def htmlId = htmlFile.name[7..-6]
	resource ( identifier: 'r'+ htmlId, type: 'webcontent', href: htmlFile.name){
	  file(href:htmlFile.name)
	  (new File("lesson_${htmlId}.md").text =~ /!\[.*\]\((.*)\)/).each{ full, match ->
	    file(href:match)
	  }
	  file(href:'styles.css')
	}
      }
    } 
}
}

