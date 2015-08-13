#! /bin/csh

    set a="a.txt";
    set b="b.txt";

    set av="a.vcb";
    set bv="b.vcb"
    set ab="a_b.snt";

    ./plain2snt.out $a $b
    ./snt2plain.out $av $bv $ab PLAIN

    ./mkcls -m2 -pPLAIN1.txt -c50 -V$av.classes opt >& mkcls1.log
    rm PLAIN1.txt
    ./mkcls -m2 -pPLAIN2.txt -c50 -V$bv.classes opt >& mkcls2.log
    rm PLAIN2.txt
    ./snt2cooc.out $av $bv $ab > co.cooc
    ./GIZA++ -S $av -T $bv -C $ab -p0 0.98 -CoocurrenceFile co.cooc  -o fereshte >& fereshte.log

