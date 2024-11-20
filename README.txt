# ElasticSearch phonetic search plugin for [Nyingarn](https://nyingarn.net)

## Algorithm description: (not comprehensive)
1. replace unicode diacritics with ASCII versions
2. remove macron from vowels
3. replace diphthong combinations with `-y-`/`-w-`and initial i/u-&gt;yi/wu
4. `-ow/-aw` -&gt; AWU
5. `ah` `er` `uh` `ar` -&gt; A
6. EN/EL -&gt; IN/IL
7. long vowel -&gt; short vowel
8. UA/UI/OA -&gt; UWA/UWI
9. remove double letters (except for `rr`)
10. enye -&gt; NG
11. YNY/YLY/YN/YL -&gt; NY/LY
12. double digraphs -&gt; single digraph (e.g., NGNG -&gt; NG)
13. final Y -&gt; AYI
14. initial G -&gt; K
15. single letter replacements
  i. B -&gt; P
  ii. D -&gt; T
  iii. O -&gt; U
  iv. E -&gt; I
16. G -&gt; K (except for `NG`)
17. di- and tri-graphs -&gt; J
18. S -&gt; J
19. C -&gt; K, WH -&gt; W
20. WU -&gt; U (except initially or after `A` `I` or `U`)
21. WA/WI/WU -&gt; AWA/AWI/AWU (except initially or after `AIU`)
22. RA/RI/RU -&gt; URA/URI/URU (except initially or after `AIUR`)
23. Y -&gt; AYI (except next to `AIU`, or after `N` or `L`)
24. AY -&gt; AYI (except before `AIU`)
25. Y -&gt; I (except after `AIUNLT`)
26. remove special characters and digits
