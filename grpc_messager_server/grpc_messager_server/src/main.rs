extern crate postgres;
use postgres::{ Client, Error, NoTls, Statement };
use tokio::sync::broadcast::error;



struct Users 
{
    email: String,
    phone: String,
    info: String,
    surname: String,
    name: String,
    middle_name: String
}
fn main() 
{
    let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
    let mut client = Client::connect(con_string,NoTls).unwrap();
    //let stmt = client.prepare("select* from users");

for row in &client.query("select* from auth('danil1337', 'danil2002')", &[]).unwrap() 
{
    let user = Users
    {
    email:row.get(0),
    phone:row.get(1), 
    info:row.get(2),
    surname:row.get(3), 
    name:row.get(4),
    middle_name:row.get(5)
    };
    println!("Found student {} with ID:{}", user.email, user.surname);
}


    
    // let email= row.get(0);
    //     let  phone= row.get(1); 
    //     let  info= row.get(2);
    //     let  surname= row.get(3); 
    //     let  name= row.get(4); 
    //     let  middle_name= row.get(5);
 
    //      println!("Record: {:?} {:?} {:?} {:?} {:?} {:?}", email, phone, info, surname, name, middle_name);

    // let conn =
    // Connection::connect(
    //     "postgres://postgres:postgres@localhost",
    //     &SslMode::None)
    // .unwrap();

    //let mut client = Client::connect("host=localhost user=postgres", NoTls)?;
    //for row in client.query("select* from auth('danil1337', 'danil2002')", &[])? 
    //let stmt = conn.prepare("select * from auth('danil1337', 'danil2002')").unwrap();
    //for row in stmt.query(&[]).unwrap() {
    //    println!("found person: {}", row);
    //}
    // for row in conn.query("select * from auth('danil1337', 'danil2002')", &[])? {

    //     // let id: i32 = row.get(0);
    //     // let name: &str = row.get(1);
    //     // let data: Option<&[u8]> = row.get(2);

    //     println!("found person: {}", row);
    // }
    
}

