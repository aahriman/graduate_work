echo "==INSTALL PYTHON MODULES==="

#to install pyfst
if [[ ! $(pip3 freeze | grep -i pyfst) ]]
then
  echo "installation pyfst"
  mkdir openfst
  cd openfst && (
  wget http://www.openfst.org/twiki/pub/FST/FstDownload/openfst-1.3.4.tar.gz
  tar zxf openfst-1.3.4.tar.gz
  cd openfst-1.3.4 && (
  ./configure
  make
  make install
  pip3 install pyfst
  ldconfig
  ))
fi

#to install pymysql
if [[ ! $(pip3 freeze | grep -i pymysql) ]]
then
  echo "installation pymysql"
  pip3 install pymysql
fi

#to install flask
if [[ ! $(pip3 freeze | grep -i flask) ]]
then
  echo "installation flask"
  pip3 install flask 
fi

