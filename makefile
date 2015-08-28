lessons_html = $(foreach f, $(basename $(wildcard lesson_*.md) .md), $f.html)

%.zip: $(lessons_html) *.png *.jpg styles.css imsmanifest.xml
	groovy manifest.groovy
	zip $@ $^ 

lesson_%.html: lesson_%.md
	pandoc -s -N -c styles.css -o $@ $< #  --bibliography="../../txt/_bib/newentries.bib"

html: $(lessons_html)

%: %.md
	cp $< $<.tmp
	ex -S levelup.vim $<.tmp
	sed '/./,$$!d' $<.tmp > $<.tmp2
	csplit -s -f lesson_ $<.tmp2 "/\%\ .*/" "{*}"
	rename 's/lesson_(\d\d)$$/lesson_$$1.md/' lesson_*
	for f in $$(ls lesson_*.md | grep lesson_[0-9][0-9]\.m); do ex -S levelup.vim $$f; g=`basename $$f .md`; sed '/./,$$!d' $$f > $$g_2.md; csplit -s -f $$g- $$g_2.md "/\%\ .*/" "{*}"; done
	rm lesson_*.md
	rename 's/lesson_(\d\d-\d\d)$$/lesson_$$1.md/' lesson_*-*
	rm $<.tmp $<.tmp2

clean:
	rm lesson_*.md lesson_*.html 

presentation.pdf: presentation.md
	pandoc -N --chapters --slide-level 4 -t beamer -o presentation.pdf presentation.md

