SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

apt-get install mysql-server apache2 python3-pip

bash "$SCRIPT_DIR/install_python_modules.sh"
bash "$SCRIPT_DIR/create_database.sh"
bash "$SCRIPT_DIR/create_record_data.sh"
