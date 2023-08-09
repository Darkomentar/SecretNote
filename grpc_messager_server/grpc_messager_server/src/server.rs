

use lettre::transport::smtp::authentication::Credentials;
use prost::encoding::bytes;
use tonic::{transport::Server, Request, Response, Status};
use tokio_postgres::{NoTls, Error, Row};
use testproto::nuzhniitest_server::{Nuzhniitest, NuzhniitestServer};
use testproto::{NuzhniiResponse, NuzhniiRequest};
use testproto::{LoginResponse, LoginRequest};
use testproto::{EmailConfirmationResponse, EmailConfirmationRequest};
use testproto::{RegistrationRequest, SeccessfullResponse,LoginConnectResponse, LoginConnectRequest,ChatListRequest,ChatListResponse};
use testproto::{SendMsgRequest, SendMsgResponse};
use testproto::{GetMsgRequest, GetMsgResponse, GetMsgStreamRequest, GetMsgStreamResponse, GetMsgPartStreamRequest,GetMsgPartStreamResponse, GetLoginsStreamResponse, GetLoginsRequest};
use testproto::{GetLastReadedResponse, GetLastReadedRequest};
use testproto::{ChatListSRequest, ChatListSResponse};
use testproto::{CheckNewMsgRequest, CheckNewMsgResponse};
use testproto::{GetPublickKeyRequest, GetPublickKeyResponse};
use testproto::{CreateGroupChatRequest, CreateGroupChatResponse};  
use testproto::{AddGroupChatRequest, AddGroupChatResponse};
use testproto::{GetMsgGroupStreamRequest, GetMsgGroupStreamResponse};
use testproto::{GetMsgGroupPartStreamRequest, GetMsgGroupPartStreamResponse};
use testproto::{GetLastReadedGroupRequest, GetLastReadedGroupResponse};
use testproto::{SetMsgGroupChatRequest, SetMsgGroupChatResponse};
use testproto::{GetPuPrKeyGroupChatRequest, GetPuPrKeyGroupChatResponse};
use testproto::{GetLoginsGroupChatRequest, GetLoginsGroupChatStreamResponse};
use testproto::{AddUserGroupChatRequest, AddUserGroupChatResponse};
use testproto::{GetUsersChatRequest, GetUsersChatResponse};
use testproto::{QuitGroupChatRequest, QuitGroupChatResponse};
use testproto::{QuitChatRequest, QuitChatResponse};
use std::io::BufReader;
use std::io::prelude::*;
use rustbitmap::BitMap;
use rustbitmap::Rgba;
use tokio_stream::wrappers::ReceiverStream;
use prost_types::{Timestamp};
use tokio::sync::mpsc;
use lettre::{Message, SmtpTransport, Transport, error};
extern crate postgres;
extern crate tokio_postgres;
extern crate regex;
use regex::Regex;
use std::time::{SystemTime, UNIX_EPOCH};
use postgres_types::FromSql;
use tokio_postgres::types::Type;
use std::iter::Iterator;
use serde::{Deserialize, Serialize};
use std::fs::File;
use std::io::Read;
use bmp::{Image, Pixel};


pub mod testproto {
    tonic::include_proto!("testproto");
}

static mut GLOBAL_VAR: i32 = 1;

#[derive(Debug, Default)]
pub struct NuzhniitestService {}

// trait Nuzhniitest {
//     type GetMsgSStream: Stream<Item = Result<super::GetMsgStreamResponse, tonic::Status>>
//     + Send
//     + Sync
//     + 'static = mpsc::Receiver<Result<GetMsgStreamResponse, Status>>;
// }


#[tonic::async_trait]
impl Nuzhniitest for NuzhniitestService {

    async fn superfunc(    &self,   request: Request<NuzhniiRequest>,) -> Result<Response<NuzhniiResponse>, Status> {
        println!("Вот такой запрос пришел: {:?}", request);
      //  println!("ggg: {:?}", request);
        let req = request.into_inner();

        let reply = NuzhniiResponse {
            message: format!("Здарова  {} твой номер: {}.", req.message, req.number).into(),
        };

        Ok(Response::new(reply))
    }

    async fn login( &self,  request: Request<LoginRequest>, ) -> Result<Response<LoginResponse>, Status> {

        println!("Пришел запрос на вход: {:?} :::: {:?}", request.get_ref().login, request.get_ref().password);
        let req = request.into_inner();
        
        
        struct Users 
        {
            email: String,
            phone: String,
            info: String,
            surname: String,
            name: String,
            middle_name: String
        }
        

        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let mut client222 = connect().await;
        let client = client222.unwrap();
        let q = client.query("select* from auth($1, $2)",  &[&req.login, &req.password]).await;

        let mut user=  Users {

            email :String::from(""),
            phone :String::from(""),
            info :String::from(""),
            surname :String::from(""),
            name :String::from(""),
            middle_name :String::from("")
        };

        let mut entered= false;

        let mut i = 0;

        for row1 in q
        {
            for row in row1
            {
                user = Users
                {
                email:row.get(0),
                phone:row.get(1), 
                info:row.get(2),
                surname:row.get(3), 
                name:row.get(4),
                middle_name:row.get(5)
                };
                entered = true;
                println!("Found student {} {} {} {} {} {}", user.email, user.phone, user.info, user.surname, user.name, user.middle_name);
                i+=1;
            }
        }
        let mut reply = LoginResponse {
                    entered: false.into(),
                    email: "false".into(),
                    phone: "false".into(),
                    info: "false".into(),
                    surname: "false".into(),
                    name: "false".into(),
                    middle_name: "false".into()
                };
        if entered {
            reply = LoginResponse {
            entered: true.into(),
            email: user.email.into(),
            phone: user.phone.into(),
            info: user.info.into(),
            surname: user.surname.into(),
            name:user.name.into(),
            middle_name: user.middle_name.into(),
            }
        }
     
    
        Ok(Response::new(reply))
    }

    async fn login_connect( &self,  request: Request<LoginConnectRequest>, ) -> Result<Response<LoginConnectResponse>, Status> {
        let req = request.into_inner();
        println!("Пришел запрос на вход: {:?} :::: {:?}", req.login, req.password);
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let mut client222 = connect().await;
        let client = client222.unwrap();
        let q = client.query("select* from login($1, $2)",  &[&req.login, &req.password]).await;
        let mut errorr= 0;
        for row1 in q
        {
            for row in row1
            {
                errorr = row.get(0);
                println!("Error code {}", errorr);

            }
        }
        let mut reply = LoginConnectResponse {
            errorcode: errorr.into()
                };

        Ok(Response::new(reply))
    } 

    async fn email_confirmation( &self, request: Request<EmailConfirmationRequest>,) -> Result<Response<EmailConfirmationResponse>, Status> {
       // println!("Пришел запрос на подтверждение email: {:?} ", request.get_ref().email);
        let req = request.into_inner();
        println!("Пришел запрос на подтверждение email: {:?} ", req.email);
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let mut client222 = connect().await;
        let client = client222.unwrap();
        let now = SystemTime::now();
        let since_the_epoch = now.duration_since(UNIX_EPOCH).expect("Time went backwards");
        let code_key = (since_the_epoch.as_millis() % 900000) + 100000;
        let strrrrrrrr = "select* from add_reg_check('".to_string() + &req.email + "'," + &code_key.to_string() + ")";
        let q2 = client.query(&strrrrrrrr, &[]).await;
        let mut errorrrr = 0;
        for row1 in q2
        {
            for row in row1
            {
                errorrrr = row.get(0);
                println!("Email exisst {} ", errorrrr);
                if errorrrr == 0
                {
                    errorrrr = email_transport(&req.email.as_str(), &code_key.to_string());
                    println!("errr_transport {} ", errorrrr);
                }
            }
        }
       
        println!("{}",errorrrr);             
        let mut reply: EmailConfirmationResponse = EmailConfirmationResponse {
            errorcode: errorrrr.into()     
                };
        println!("{}",errorrrr);
        Ok(Response::new(reply))
                }

    async fn registration( &self, request: Request<RegistrationRequest>,) -> Result<Response<SeccessfullResponse>, Status>{
        println!("Пришел запрос на подтверждение email CODA: {:?} ", request.get_ref().email);
        let req = request.into_inner();

        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let mut client222 = connect().await;
        let client = client222.unwrap();
       // let img:DynamicImage = 
       
       // let img = image::open("3853.jpg").unwrap();
        // let (width, height) = img.dimensions();
        // let mut bytes = vec![0u8; (width * height * 3) as usize];
    
        // // копируем пиксели изображения в вектор байтов
        // // img.into_bgra8().iter().enumerate().for_each(|(i, pixel)| {
        // //     bytes[i] = *pixel;
        // // });

        // for (i, pixel) in image.pixels().enumerate() {
        //     bytes[i * 3] = pixel.0[0];
        //     bytes[i * 3 + 1] = pixel.0[1];
        //     bytes[i * 3 + 2] = pixel.0[2];
        // }
     
    //    let mut file = File::open("3853.jpg").expect("Failed to open image file");
        let mut file = File::open("3853.bmp")?;
        let mut buf_reader = BufReader::new(file);
        // Читаем содержимое файла в вектор байтов
        let mut contents: Vec<u8> = Vec::new();
        buf_reader.read_to_end(&mut contents).expect("Не удалось прочитать файл");
    //    buf_reader.re
        // Преобразуем содержимое в байты
         let bytes = contents;
    
        // // Выводим содержимое в байтах
        // for byte in bytes {
        //     println!("{}", byte);
        // }
        // // определить размер файла
        // let mut buffer = [0; 4];
        // file.read_exact(&mut buffer)?;
        // let file_size = u32::from_le_bytes(buffer);

        // // прочитать BMP содержимое в байтовый массив
        // let mut bmp_data = vec![0; file_size as usize];
        // file.read_exact(&mut bmp_data)?;

        // // сохранить BMP содержимое в байтовый массив
        // let bitmap = bmp_data[54..].to_vec(); // пропустить заголовок BMP (54 байта)

        // // преобразовать bitmap в байтовый массив
        // let bytes = bitmap.as_slice();

       // let bytes =  any_as_u8_slice(&bitmap);
        //let bytes:bytes = bitmap.get
   
  
        // Читаем содержимое файла в байтовый массив
        // let mut buffer = Vec::new();
        // file.read_to_end(&mut buffer).expect("Failed to read image file");
    
        // // Загружаем изображение из байтового массива
        // let image = image::load_from_memory(&buffer).expect("Failed to decode image");
        
        // // Преобразуем изображение в байтовый массив
        // let bytes = image.to_bytes();
       // let bytes = bmp.to_vec();
        let stmt = client.prepare("select*  from add_user(($1), ($2), ($3), ($4), ($5), ($6), ($7), ($8), ($9))").await;
        let  result = client.query(&stmt.unwrap(), &[ &req.login, &req.password, &req.email,&req.surname,&req.name,&req.middle_name,&req.publickey,&req.confirmcode,&bytes]).await.unwrap();


        // let strrrrrrrr2 = "select* from add_user('".to_string() + &req.login + "','"  + &req.password + "','" + &req.email + "' ,'" + &req.surname + "','"  + &req.name + "','" + &req.middle_name + "','"  + &req.publickey + "', " + &req.confirmcode.to_string() + ""&bytes")";
        // println!("strrrrrrrr2 {} ", strrrrrrrr2);
        // let result = client.query(&strrrrrrrr2, &[]).await;
        let mut errorrr: i32 = 1;
        println!("pre for row {}", &req.confirmcode);
        for row in result
        {
          
                errorrr = row.get(0);
                println!("error {} ", errorrr);
            
        }
        println!("[post] for row");
        let mut reply: SeccessfullResponse = SeccessfullResponse {
            errorcode: errorrr.into()        // 1 - неверный код 2 - ошибка добавления пользователя 
        };
        Ok(Response::new(reply))
        }

        // async fn chat_list( &self, request: Request<ChatListRequest>,) -> Result<Response<ChatListResponse>, Status>{
        //     println!("Пришел запрос на список чатов: {:?}    {:?}", request.get_ref().login,request.get_ref().password );
        //     let req = request.into_inner();
        //     // let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        //     // let mut client222 = connect().await;
        //     // let client = client222.unwrap();
        //     let login: &str = "Jober";
        //     let password  = "JoberPass";
        //     let chats  = ["хопхей лалалей", "Труляля"];
        //     let mut entered = false;
        //    if(req.login == login) && (password == req.password)
        //    {
        //     entered = true;
        //    }
        //     let mut reply: ChatListResponse = ChatListResponse {
        //         chatunit: "Пусто".into()        // 1 - неверный код 2 - ошибка добавления пользователя 
        //     };
        //     let mut yyykgk = "1";
        //     let mut ty = 0;
        //     unsafe
        //     {
        //         GLOBAL_VAR = GLOBAL_VAR + 1;
        //         ty = GLOBAL_VAR;
        //     }
        //    // yyykgk = &GLOBAL_VAR.to_string();
        //     // let mut ty = GLOBAL_VAR;
        //     if entered
        //     {
        //         // reply.chatunit = (chats[1].to_owned() + "  " + chats[0] + &GLOBAL_VAR.to_string()).into() ; 
        //         reply.chatunit = (chats[1].to_owned() + "  " + chats[0] + &ty.to_string()).into();
        //         //reply.chatunit = (chats[1].to_owned() + "  " + chats[0] ).into() ; 
        //     }
        //     Ok(Response::new(reply))
        //     }
        
        async fn chat_list( &self, request: Request<ChatListRequest>,) -> Result<Response<ChatListResponse>, Status>{
            println!("Пришел запрос на список чатов: {:?}    {:?}", request.get_ref().login,request.get_ref().password );
            let req = request.into_inner();
            // let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            // let mut client222 = connect().await;
            // let client = client222.unwrap();
            let login: &str = "Jober";
            let password  = "JoberPass";
            let chats  = ["хопхей лалалей", "Труляля"];
            let mut entered = false;
           if(req.login == login) && (password == req.password)
           {
            entered = true;
           }
            let mut reply: ChatListResponse = ChatListResponse {
                chatunit: "Пусто".into()        // 1 - неверный код 2 - ошибка добавления пользователя 
            };
            let mut yyykgk = "1";
            let mut ty = 0;
            unsafe
            {
                GLOBAL_VAR = GLOBAL_VAR + 1;
                ty = GLOBAL_VAR;
            }
           // yyykgk = &GLOBAL_VAR.to_string();
            // let mut ty = GLOBAL_VAR;
            if entered
            {
                // reply.chatunit = (chats[1].to_owned() + "  " + chats[0] + &GLOBAL_VAR.to_string()).into() ; 
                reply.chatunit = (chats[1].to_owned() + "  " + chats[0] + &ty.to_string()).into();

                //reply.chatunit = (chats[1].to_owned() + "  " + chats[0] ).into() ; 
            }
            Ok(Response::new(reply))
            }
 
        async fn send_msg( &self, request: Request<SendMsgRequest>,) -> Result<Response<SendMsgResponse>, Status>{
            println!("Пришел запрос на сотправку сообщения: {:?}    {:?}", request.get_ref().login,request.get_ref().password );
            let req = request.into_inner();
            

            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            // let mut client = connect().await.unwrap();
            // let request_sql = "select* from send_message('".to_string() + &req.recipientid.to_string()+ "','"  + &req.sender_msg + "','" + &req.recipient_msg + "' ,'" + &req.r#type + "','"  + &req.login + "','" + &req.password  + ")";
            // println!("request_sql {} ", request_sql);
            // let result = client.query(&strrrrrrrr2, &[]).await;
            let client = connect().await.unwrap();
            let stmt = client.prepare("select* from send_message( ($1), ($2), ($3) ,($4),($5),($6))").await;
            let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.sender_msg, &req.recipient_msg, &req.typemsg, &req.login, &req.password]).await.unwrap();
            let mut errorcode:i32 = 2;
            for row1 in y
            {
                errorcode = row1.get(0);
                //errorcode = errorcode_for;
                 println!("error {} ", errorcode);
            }

            let mut reply: SendMsgResponse = SendMsgResponse {
                errorcode: errorcode.into()       
            };
            // 1 - если логин или пароль не верны
            // 0 - если все ок
            // 2 - если ошибка отправки сообщения или пишешь самому себе 
            Ok(Response::new(reply))
            }

        async fn get_msg( &self, request: Request<GetMsgRequest>,) -> Result<Response<GetMsgResponse>, Status>{
            println!("Пришел запрос на получение сообщения: {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            // let stmt = client.prepare("select* from send_message( ($1), ($2), ($3) ,($4),($5),($6))").await;
            // let  y = client.query(&stmt.unwrap(), &[&req.recipientid, &req.sender_msg, &req.recipient_msg, &req.typemsg, &req.login, &req.password]).await.unwrap();
            // let mut errorcode:i32 = 2;
            // for row1 in y
            // {
            //     errorcode = row1.get(0);
            //     println!("error {} ", errorcode);
            // let (mut tx, rx) = mpsc::channel();
           // let (mut tx, rx) = mpsc::channel(4);
           let now = chrono::Utc::now();
           //let ff: Timestamp = now.to_rfc3339();
           let timestamp = Timestamp {
               seconds: now.timestamp(),
               nanos: now.timestamp_subsec_nanos() as i32,
           };

            let errorcode = 1;
            let MsgStre = r#"Месседж блеать"#;
            let MsgByte = MsgStre.as_bytes();
         
            let mut reply: GetMsgResponse = GetMsgResponse {
                errorcode: errorcode.into(),
                geter_msg: MsgByte.into(),
                types: "text".into(),
                datatime: "timestamp".into()
            };
            Ok(Response::new(reply))
        }

        type GetMsgSStream = ReceiverStream<Result<GetMsgStreamResponse, Status>>;
        async fn get_msg_s( &self, request: Request<GetMsgStreamRequest>,) -> Result<Response<Self::GetMsgSStream>, Status>{
            println!("Пришел запрос на получение сообщения (между 2 людьми): {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 0;
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_dialog_messages_time_str( ($1), ($2), ($3))").await;
                let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password]).await.unwrap();
                   for row1 in y
                        {
                            let typeMsgorErorrCode: String = row1.get(2);
                            if (typeMsgorErorrCode=="2")||(typeMsgorErorrCode=="1")
                               {    
                                    errorcode = typeMsgorErorrCode.parse::<i32>().unwrap();
                                    println!("error {} ", errorcode);
                                }
                            else  {
                                errorcode = 0;
                            
                                let bb:&[u8] = row1.get(0);
                                let tt:String = row1.get(2);
                                let mm:bool = row1.get(3);
                                let dd: String = row1.get(1);
                                let uidmsg: i64 = row1.get(4);
                                let readed: bool = row1.get(5);
                                tx.send(Ok(GetMsgStreamResponse {
                                    errorcode: errorcode.into(),
                                    geter_msg: bb.into(),
                                    types:  tt.into(),
                                    datatime:  dd.into(),
                                    mymessage: mm.into(),
                                    idmsg: uidmsg.into(),
                                    readed: readed.into()
                                })).await.unwrap();
                            }
                        }
    
                        
                           
            });

           //1 не верный логин пароль
           //2 нет такого себеседника
           // 0 все чикипуки
            Ok(Response::new(rx.into()))
        }
       
        type GetMsgSPartStream = ReceiverStream<Result<GetMsgPartStreamResponse, Status>>;
        async fn get_msg_s_part( &self, request: Request<GetMsgPartStreamRequest>,) -> Result<Response<Self::GetMsgSPartStream>, Status>{
            println!("Пришел запрос на получение сообщения парт (между 2 людьми): {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let errorcode = 1;
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 2;
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_dialog_messages_time_str_check_id( ($1), ($2), ($3), ($4))").await;
                let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password, &req.idmsg_last]).await.unwrap();
                for row1 in y
                {
                    let typeMsgorErorrCode: String = row1.get(2);
                    if (typeMsgorErorrCode=="2")||(typeMsgorErorrCode=="1")
                       {    
                            errorcode = typeMsgorErorrCode.parse::<i32>().unwrap();
                            println!("error {} ", errorcode);
                        }
                    else  {
                        errorcode = 0;
                    
                        let bb:&[u8] = row1.get(0);
                        let tt:String = row1.get(2);
                        let mm:bool = row1.get(3);
                        let dd: String = row1.get(1);
                        let uidmsg: i64 = row1.get(4);
                        let readed: bool = row1.get(5);
                        tx.send(Ok(GetMsgPartStreamResponse {
                            errorcode: errorcode.into(),
                            geter_msg: bb.into(),
                            types:  tt.into(),
                            datatime:  dd.into(),
                            mymessage: mm.into(),
                            idmsg: uidmsg.into(),
                            readed: readed.into()
                        })).await.unwrap();
                    }
                }
            });
           //1 не верный логин пароль
           //2 нет такого себеседника
           //0 все чикипуки
            Ok(Response::new(rx.into()))
        }
   
        type GetLoginsStream = ReceiverStream<Result<GetLoginsStreamResponse, Status>>;
        async fn get_logins( &self, request: Request<GetLoginsRequest>,) -> Result<Response<Self::GetLoginsStream>, Status>{
            println!("Пришел запрос на поиск собеседника: {:?}  ", request.get_ref().login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 1;
          //  if (req.login != ""){
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_logins(($1))").await;
                let  y = client.query(&stmt.unwrap(), &[&req.login]).await.unwrap();
                for row1 in y
                {
                    errorcode = 0;
                    let bb:String = row1.get(0);
                    println!("{:?}", bb);
                    tx.send(Ok(GetLoginsStreamResponse {
                        errorcode: errorcode.into(),
                        login: bb.into()
                    })).await.unwrap();
                }
            });
       // }
           //1 нет таких
           //0 все чикипуки
            Ok(Response::new(rx.into()))
        }

        async fn get_last_readed( &self, request: Request<GetLastReadedRequest>,) -> Result<Response<GetLastReadedResponse>, Status>{
           
                println!("Пришел запрос на проверку прочитанных сообщений: {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
                let req = request.into_inner();
                let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_last_red_msg_id( ($1), ($2), ($3))").await;
                let y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password]).await.unwrap();
                let mut errorcode:i64 = 1;
                for row1 in y
                {
                    errorcode = row1.get(0);
                
                }
                let mut reply: GetLastReadedResponse = GetLastReadedResponse {
                    idmsg_last_readed: errorcode.into()
                };
                Ok(Response::new(reply))
        }

        type ChatListSStream = ReceiverStream<Result<ChatListSResponse, Status>>;
        async fn chat_list_s( &self, request: Request<ChatListSRequest>,) -> Result<Response<Self::ChatListSStream>, Status>{
            println!("Пришел запрос на получение списка чатов: {:?}    {:?} ", request.get_ref().login,request.get_ref().password);
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 0;
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_dialogs( ($1), ($2)) ORDER BY timeee DESC").await;
                let y = client.query(&stmt.unwrap(), &[&req.login, &req.password]).await.unwrap();
                for row1 in y
                {
                    let typeMsgorErorrCode: i32 = row1.get(5);
                    if typeMsgorErorrCode<=-1
                       {    
                        let mut vvv: i32 = 1;
                        let photo:&[u8]  =  &vvv.to_be_bytes()[..];
                        println!("ошибка: {:?} ", typeMsgorErorrCode);
                            tx.send(Ok(ChatListSResponse {
                                errorcode: typeMsgorErorrCode.into(),
                                login: "Login".into(),
                                lastname:  "Lastname".into(),
                                firstname:  "Firstname".into(),
                                middlename: "Middlename".into(),
                                photo: photo.into(),
                                countmsg: 0.into(),
                                lastidmsg: 0.into(),
                                group_chat: false.into()
                            })).await.unwrap();
                        }
                    else  {
                        errorcode = 0;
                        let Login: String = row1.get(0);
                        let Lastname: String = row1.get(2);
                        let Firstname: String = row1.get(3);
                        let Middlename: String = row1.get(4);
                        let photo:&[u8]  = row1.get(1);
                        let countmsg: i32 = row1.get(5);
                        let lastidmsg: i64 = row1.get(6);
                        let group_chat: bool = row1.get(7);
                        tx.send(Ok(ChatListSResponse {
                            errorcode: errorcode.into(),
                            login: Login.into(),
                            lastname:  Lastname.into(),
                            firstname:  Firstname.into(),
                            middlename: Middlename.into(),
                            photo: photo.into(),
                            countmsg: countmsg.into(),
                            lastidmsg: lastidmsg.into(),
                            group_chat: group_chat.into()
                        })).await.unwrap();
                    }
                }
                
                
            });

           //1 не верный логин пароль
           //2 нет такого себеседника
           // 0 все чикипуки
            Ok(Response::new(rx.into()))
        }


        async fn check_new_msg( &self, request: Request<CheckNewMsgRequest>,) -> Result<Response<CheckNewMsgResponse>, Status>{
           
            println!("Пришел запрос на проверку новых сообщений (список чатов): {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().idlastmsg );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let stmt = client.prepare("select*  from check_new_msg( ($1), ($2), ($3), ($4))").await;
            let y = client.query(&stmt.unwrap(), &[&req.login, &req.password, &req.idlastmsg, &req.idlastmsggroup]).await.unwrap();
            let mut errorcode:i32 = 1;
            for row1 in y
            {
                errorcode = row1.get(0);
         
            
            }
 
            let mut reply: CheckNewMsgResponse = CheckNewMsgResponse {
                errorcode_oridlastmsg: errorcode.into()
            };
            Ok(Response::new(reply))
    }

        async fn get_publick_key( &self, request: Request<GetPublickKeyRequest>,) -> Result<Response<GetPublickKeyResponse>, Status>{
           
        println!("Пришел запрос на пполучение публичного ключа: {:?}", request.get_ref().login);
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from get_public_key( ($1))").await;
        let y = client.query(&stmt.unwrap(), &[&req.login]).await.unwrap();
        let mut errorcode:String = "-1".to_string();
        for row1 in y
        {
            errorcode = row1.get(0);
        }
        let mut reply: GetPublickKeyResponse = GetPublickKeyResponse {
            errorcode_or_publick_key: errorcode.into()
        };
        Ok(Response::new(reply))
        }


        async fn create_group_chat( &self, request: Request<CreateGroupChatRequest>,) -> Result<Response<CreateGroupChatResponse>, Status>{
           
            println!("Пришел запрос на создание чата: {:?}", request.get_ref().login);
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let stmt = client.prepare("select*  from create_group_chat(($1),($2),($3),($4),($5),($6),($7),($8))").await;


            let mut file = File::open("3854.bmp")?;
            let mut buf_reader = BufReader::new(file);
            let mut contents: Vec<u8> = Vec::new();
            buf_reader.read_to_end(&mut contents).expect("Не удалось прочитать файл");

             let bytes = contents;
        



            let y = client.query(&stmt.unwrap(), &[&req.name_chat,&req.login_chat,&req.public_key,&req.private_key,&req.login,&req.password, &bytes, &req.first_msg]).await.unwrap();
            let mut errorcode:i32 = 0;
            for row1 in y
            {
                errorcode = row1.get(0);
            
            }
            let mut reply: CreateGroupChatResponse = CreateGroupChatResponse {
                errorcode: errorcode.into()
            };
            Ok(Response::new(reply))
            }


        async fn add_group_chat( &self, request: Request<AddGroupChatRequest>,) -> Result<Response<AddGroupChatResponse>, Status>{
                
            println!("Пришел запрос на добавление пользователя в чат: {:?}", request.get_ref().login);
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let stmt = client.prepare("select*  from add_group_chat(($1),($2),($3),($4),($5))").await;
            let y = client.query(&stmt.unwrap(), &[&req.login_add_user, &req.private_key, &req.login_add_user, &req.login, &req.password]).await.unwrap();
            let mut errorcode:i32 = 0;
            for row1 in y
            {
                errorcode = row1.get(0);
            
            }
            
            let mut reply: AddGroupChatResponse = AddGroupChatResponse {
                errorcode: errorcode.into()
            };
            Ok(Response::new(reply))
             //-1 нет такого добавляемого пользователя
            // -2  не верный логин пароль
            // - 4 нет такого чата иил вы не владелец чата
            //-5 пользователь уже есть в чате 
            }
           

        type GetMsgGroupSStream = ReceiverStream<Result<GetMsgGroupStreamResponse, Status>>;
        async fn get_msg_group_s( &self, request: Request<GetMsgGroupStreamRequest>,) -> Result<Response<Self::GetMsgGroupSStream>, Status>{
            println!("Пришел запрос на получение сообщения (всех сообщений в групповом чате): {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 0;
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_group_dialog_messages( ($1), ($2), ($3))").await;
                let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password]).await.unwrap();
                   for row1 in y
                        {
                            let typeMsgorErorrCode: String = row1.get(2);
                            if (typeMsgorErorrCode=="2")||(typeMsgorErorrCode=="1")
                               {    
                                    errorcode = typeMsgorErorrCode.parse::<i32>().unwrap();
                                    println!("error {} ", errorcode);
                                }
                            else  {
                                errorcode = 0;
                            
                                let bb:&[u8] = row1.get(0);
                                let tt:String = row1.get(2);
                                let mm:bool = row1.get(3);
                                let dd: String = row1.get(1);
                                let uidmsg: i64 = row1.get(4);
                                let readed: bool = row1.get(5);
                                let autor: String = row1.get(6);
                                tx.send(Ok(GetMsgGroupStreamResponse {
                                    errorcode: errorcode.into(),
                                    geter_msg: bb.into(),
                                    types:  tt.into(),
                                    datatime:  dd.into(),
                                    mymessage: mm.into(),
                                    idmsg: uidmsg.into(),
                                    readed: readed.into(),
                                    autor: autor.into()
                                })).await.unwrap();
                            }
                        }
    
                        
                           
            });

           //1 не верный логин пароль
           //2 нет такого чата
           // 0 все чикипуки
            Ok(Response::new(rx.into()))
        }

        type GetMsgGroupSPartStream = ReceiverStream<Result<GetMsgGroupPartStreamResponse, Status>>;
        async fn get_msg_group_s_part( &self, request: Request<GetMsgGroupPartStreamRequest>,) -> Result<Response<Self::GetMsgGroupSPartStream>, Status>{
            println!("Пришел запрос на получение сообщения парт (в групповом чате): {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
            let req = request.into_inner();
            let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
            let client = connect().await.unwrap();
            let errorcode = 1;
            let (mut tx, rx) = mpsc::channel(4);
            let mut errorcode:i32 = 2;
            tokio::spawn(async move {
                let client = connect().await.unwrap();
                let stmt = client.prepare("select*  from get_group_dialog_messages_part( ($1), ($2), ($3), ($4))").await;
                let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password, &req.idmsg_last]).await.unwrap();
                for row1 in y
                {
                    let typeMsgorErorrCode: String = row1.get(2);
                    if (typeMsgorErorrCode=="2")||(typeMsgorErorrCode=="1")
                       {    
                            errorcode = typeMsgorErorrCode.parse::<i32>().unwrap();
                            println!("error {} ", errorcode);
                        }
                    else  {
                        errorcode = 0;
                    
                        let bb:&[u8] = row1.get(0);
                        let tt:String = row1.get(2);
                        let mm:bool = row1.get(3);
                        let dd: String = row1.get(1);
                        let uidmsg: i64 = row1.get(4);
                        let readed: bool = row1.get(5);
                        let autor: String = row1.get(6);
                        tx.send(Ok(GetMsgGroupPartStreamResponse {
                            errorcode: errorcode.into(),
                            geter_msg: bb.into(),
                            types:  tt.into(),
                            datatime:  dd.into(),
                            mymessage: mm.into(),
                            idmsg: uidmsg.into(),
                            readed: readed.into(),
                            autor: autor.into()
                        })).await.unwrap();
                    }
                }
            });
           //1 не верный логин пароль
           //2 нет такого себеседника
           //0 все чикипуки
            Ok(Response::new(rx.into()))
        }

//select* from get_last_read_msg_id_group('LoginChat1', 'Danil', 'Danil')
//    rpc GetLastReadedGroup (GetLastReadedGroupRequest) returns (GetLastReadedGroupResponse);

    async fn get_last_readed_group( &self, request: Request<GetLastReadedGroupRequest>,) -> Result<Response<GetLastReadedGroupResponse>, Status>{
           
        println!("Пришел запрос на проверку прочитанных сообщений: {:?}    {:?} uid   {:?} ", request.get_ref().login,request.get_ref().password , request.get_ref().recipient_login );
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from get_last_read_msg_id_group( ($1), ($2), ($3))").await;
        let y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.login, &req.password]).await.unwrap();
        let mut errorcode:i64 = 1;
        for row1 in y
        {
            errorcode = row1.get(0);
        
        }
        let mut reply: GetLastReadedGroupResponse = GetLastReadedGroupResponse {
            idmsg_last_readed: errorcode.into()
        };
        Ok(Response::new(reply))
    }

    async fn set_msg_group_chat( &self, request: Request<SetMsgGroupChatRequest>,) -> Result<Response<SetMsgGroupChatResponse>, Status>{
        println!("Пришел запрос на сотправку сообщения: {:?}    {:?}", request.get_ref().login,request.get_ref().password );
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select* from send_msg_group_chat( ($1), ($2), ($3) ,($4),($5))").await;
        let  y = client.query(&stmt.unwrap(), &[&req.recipient_login, &req.msg, &req.login, &req.password, &req.typemsg]).await.unwrap();
        let mut errorcode:i32 = 2;
        for row1 in y
        {
            errorcode = row1.get(0);
            println!("error {} ", errorcode);
        }

        let mut reply: SetMsgGroupChatResponse = SetMsgGroupChatResponse {
            errorcode: errorcode.into()       
        };
        // 1 - если логин или пароль не верны
        // 0 - если все ок
        // 2 - если ошибка отправки сообщения или пишешь самому себе 
        Ok(Response::new(reply))
    }

    

    async fn get_pu_pr_key_group_chat( &self, request: Request<GetPuPrKeyGroupChatRequest>,) -> Result<Response<GetPuPrKeyGroupChatResponse>, Status>{
           
        println!("Пришел запрос на получение привтаного и публичного ключа для группогого чата: {:?}", request.get_ref().login);
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from GetPPKeyGroupChat( ($1), ($2), ($3))").await;
        let y = client.query(&stmt.unwrap(), &[&req.login_chat, &req.login, &req.password]).await.unwrap();
        let mut errorcode:i32 = -4;
        let mut publickey:String = "-4".to_string();
        let mut privatekey:&[u8] = "-4".as_bytes();
        let mut row1 = y.get(0).unwrap();
        privatekey = row1.get(1);
        let errorcodef:String = row1.get(0);          
        if ("-1" != errorcodef)&&("-2" != errorcodef) {
            errorcode = 0;
            publickey = errorcodef;
        }
        else if "-1" != errorcodef {
            errorcode = -1;
        }
        else if "-2" != errorcodef {
            errorcode = -2;
        }
        let mut reply: GetPuPrKeyGroupChatResponse = GetPuPrKeyGroupChatResponse {
            errorcode: errorcode.into(),
            publickey: publickey.into(),
            privatekey: privatekey.into()
        };
        Ok(Response::new(reply))
    }


    type GetLoginsGroupChatStream = ReceiverStream<Result<GetLoginsGroupChatStreamResponse, Status>>;
    async fn get_logins_group_chat( &self, request: Request<GetLoginsGroupChatRequest>,) -> Result<Response<Self::GetLoginsGroupChatStream>, Status>{
        println!("Пришел запрос на поиск людей для добавления в группу: {:?}  {:?}  ", request.get_ref().login , request.get_ref().login_chat );
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let (mut tx, rx) = mpsc::channel(4);
        let mut errorcode:i32 = 1;

        tokio::spawn(async move {
            let client = connect().await.unwrap();
            let stmt = client.prepare("select*  from get_logins_group_chat(($1),($2))").await;
            let  y = client.query(&stmt.unwrap(), &[&req.login, &req.login_chat]).await.unwrap();
            for row1 in y
            {
                errorcode = 0;
                let bb:String = row1.get(0);
                println!("{:?}", bb);
                tx.send(Ok(GetLoginsGroupChatStreamResponse {
                    errorcode: errorcode.into(),
                    login: bb.into()
                })).await.unwrap();
            }
        });

    //1 нет таких
    //0 все чикипуки
        Ok(Response::new(rx.into()))
    }

    async fn add_user_group_chat( &self, request: Request<AddUserGroupChatRequest>,) -> Result<Response<AddUserGroupChatResponse>, Status>{
        // rpc AddUserGroupChat (AddUserGroupChatRequest) returns (AddUserGroupChatResponse);
        println!("Пришел запрос на доавления человека в чат: {:?}    {:?} ", request.get_ref().u_login,request.get_ref().login_chat);
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from add_group_chat( ($1), ($2), ($3), ($4), ($5))").await;
        let y = client.query(&stmt.unwrap(), &[&req.login_chat, &req.private_key, &req.u_login,  &req.login, &req.password]).await.unwrap();
        let mut errorcode:i32 = 0;
        for row1 in y
        {
            errorcode = row1.get(0);
        
        }
        let mut reply: AddUserGroupChatResponse = AddUserGroupChatResponse {
            errorcode: errorcode.into()
        };
        Ok(Response::new(reply))
    }


    type GetUsersChatStream = ReceiverStream<Result<GetUsersChatResponse, Status>>;
    async fn get_users_chat( &self, request: Request<GetUsersChatRequest>,) -> Result<Response<Self::GetUsersChatStream>, Status>{
        println!("Пришел запрос на получение списка людей в группе: {:?}  {:?}  ", request.get_ref().login , request.get_ref().login_chat );
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let (mut tx, rx) = mpsc::channel(4);
        let mut errorcode:i32 = 1;

        tokio::spawn(async move {
            let client = connect().await.unwrap();
            let stmt = client.prepare("select*  from get_users_chat(($1), ($2), ($3))").await;
            let  y = client.query(&stmt.unwrap(), &[&req.login_chat, &req.login, &req.password ]).await.unwrap();
            for row1 in y
            {
                println!("z");
                errorcode = 0;
                let l:String = row1.get(0);
                let s:String = row1.get(1);
                let n:String = row1.get(2);
                let m:String = row1.get(3);
                tx.send(Ok(GetUsersChatResponse {
                    errorcode: errorcode.into(),
                    u_login: l.into(),
                    surname: s.into(),
                    name: n.into(),
                    middle_name: m.into()
                })).await.unwrap();
            }
        });
        Ok(Response::new(rx.into()))
    }

    
//rpc QuitGroupChat(QuitGroupChatRequest) returns (QuitGroupChatResponse);
    async fn quit_group_chat( &self, request: Request<QuitGroupChatRequest>,) -> Result<Response<QuitGroupChatResponse>, Status>{
        // rpc AddUserGroupChat (AddUserGroupChatRequest) returns (AddUserGroupChatResponse);
        println!("Пришел запрос на выхода из группогово чата. Login: {:?}  Chat:  {:?} ", request.get_ref().login,request.get_ref().login_chat);
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from quit_group_chat( ($1), ($2), ($3))").await;
        let y = client.query(&stmt.unwrap(), &[&req.login_chat, &req.login, &req.password]).await.unwrap();
        let mut errorcode:i32 = 0;
        for row1 in y
        {
            errorcode = row1.get(0);
        
        }
        let mut reply: QuitGroupChatResponse = QuitGroupChatResponse {
            errorcode: errorcode.into()
        };
        Ok(Response::new(reply))
    }

    async fn quit_chat( &self, request: Request<QuitChatRequest>,) -> Result<Response<QuitChatResponse>, Status>{
        // rpc AddUserGroupChat (AddUserGroupChatRequest) returns (AddUserGroupChatResponse);
        println!("Пришел запрос на выхода из чата. Login: {:?}  Собеседник:  {:?} ", request.get_ref().login,request.get_ref().login_interlocutor);
        let req = request.into_inner();
        let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
        let client = connect().await.unwrap();
        let stmt = client.prepare("select*  from quit_chat( ($1), ($2), ($3))").await;
        let y = client.query(&stmt.unwrap(), &[&req.login_interlocutor, &req.login, &req.password]).await.unwrap();
        let mut errorcode:i32 = 0;
        for row1 in y
        {
            errorcode = row1.get(0);
        
        }
        let mut reply: QuitChatResponse = QuitChatResponse {
            errorcode: errorcode.into()
        };
        Ok(Response::new(reply))
    }

}




#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {

    let addr = "192.168.0.77:50030".parse()?;
    let nuzhnii_service = NuzhniitestService::default();
    Server::builder()
        .add_service(NuzhniitestServer::new(nuzhnii_service))
        .serve(addr)
        .await?;

    Ok(())
}


async fn connect() -> Result<tokio_postgres::Client, Error> {
    let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";

    let (client, connection) = tokio_postgres::connect(con_string, NoTls).await?;

    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("connection error: {}", e);
        }
    });

    Ok(client)
}


fn email_transport(too: &str, code_confirmation: &str) ->i32{
    let email_regex = Regex::new(r"^([a-z0-9_+]([a-z0-9_+.]*[a-z0-9_+])?)@([a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,6})").unwrap();
    if !email_regex.is_match(too)
    {
        return  4;
    }

        let email = Message::builder()
        .from("BibliotekaKursa4@yandex.ru".parse().unwrap())
        .reply_to("BibliotekaKursa4@yandex.ru".parse().unwrap())
        .to(too.parse().unwrap_or_else(|_| {
            panic!("Invalid email address: {}", 4);
        }))
        .subject("Ваш код от Secret Note")
        .body(code_confirmation.to_string())
        .unwrap();


let creds = Credentials::new("BibliotekaKursa4@yandex.ru".to_string(), "danilmailtest".to_string());

// Open a remote connection to gmail
let mailer = SmtpTransport::relay("smtp.yandex.ru")
    .unwrap()
    .credentials(creds)
    .build();

// Send the email
match mailer.send(&email) {
    Ok(_) => println!("Email sent successfully!"),
    Err(e) => panic!("Could not send email: {:?}", e),
}
return 0;
}