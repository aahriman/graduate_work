SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "== CREATE RECORDS DATA ==="

echo "path to big free dist for data dir (60GB+):"
read data_dir
mkdir "$data_dir/cs"
mkdir "$data_dir/en"
cd "$data_dir/cs" && (
  for i in $(cat $SCRIPT_DIR/../data/cs_data);  do
    wget $i;
  done
  for i in $(ls); do
     new_name=${i%?}
     mv $i $new_name
  done
  for i in $(ls *.tar*); do
     tar -xf $i;
  done
)

cd "$data_dir/en" && (
  for i in $(cat $SCRIPT_DIR/../data/en_data);  do
    wget $i;
  done
  for i in $(ls); do
     new_name=${i%?}
     mv $i $new_name
  done
  for i in $(ls *.tar*); do
     tar -xf $i;
  done
)

