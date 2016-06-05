echo "===CREATING DATABASE==="

echo "write user to database:"
read user
echo "write passwd to database:"
read passwd

files=$(ls ../database/sql/*.sql)
for i in $files; do
   echo "execute $i"
   mysql -p$passwd -u$user < $i
done

