echo `grep 'EMPTY Recall' $1 > fer2.txt`
echo `sed  's/.*= //' fer2.txt > fer3.txt`
echo `cat fer3.txt`
echo `open fer3.txt`


