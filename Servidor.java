import java.util.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.util.Locale;
import java.util.TimeZone;

public class Servidor 

{

    public static void main(String[] args) throws IOException 
	
	{
        // cria um socket "servidor" associado a porta 12345
        ServerSocket servidor = new ServerSocket(12345);
		
        //aguardando uma requisiÃ§Ã£o.
		
        while(true)
		{
            //aceita a primeira conexao
            Socket socket = servidor.accept();
            //verifica se esta conectado
			System.out.println("Servidor online");
			
            if (socket.isConnected()) 
			{
				
                //informa IP do cliente
                System.out.println(socket.getInetAddress());
                //cria um BufferedReader a partir do InputStream do cliente. 
                BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Requisição: ");
                // Lê a primeira linha, contem as informaçoes da requisição
                String linha = buffer.readLine();
                //quebra a string pelo espaço em branco
                String[] dadosRequisicao = linha.split(" ");
                //pega o metodo
                String metodo = dadosRequisicao[0];
                //paga o caminho do arquivo
                String diretorioArquivo = dadosRequisicao[1];
                //pega o protocolo
                String protocolo = dadosRequisicao[2];
                //Enquanto a linha não for vazia
				
                while (!linha.isEmpty())
				{
                    //imprime a linha
                    System.out.println(linha);
                    //lê a proxima linha
                    linha = buffer.readLine();
                }
				
                //se o caminho foi igual a / entao deve pegar o /avaliacao1.html
                if (diretorioArquivo.equals("/")) 
				{
                    diretorioArquivo = "avaliacao1.html";
                }
                
				//abre o arquivo pelo caminho
                File arquivo = new File(diretorioArquivo.replaceFirst("/", ""));
                //100 para efetuado com sucesso (arquivo existente) 
                String status = protocolo + " 100 OK\r\n";
                //muda-se o status depois enviar o arquivo de erro 404 (Arquivo Inexistente)
                
				if (!arquivo.exists()) 
				{
                    status = protocolo + " 404 Not Found\r\n";
                    arquivo = new File("404.html");
                }

                //lê todo o conteúdo do arquivo para bytes
                byte[] conteudo = Files.readAllBytes(arquivo.toPath());
                //cria um formato para o GMT espeficicado pelo HTTP
                SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date data = new Date();
                //Formata a data para o padrao
                String dataFormatada = format.format(data) + " GMT";
                //cabeçalho padrão da resposta HTTP
                String header = status
                    + "Location: http://localhost:12345/\r\n"
                    + "Date: " + dataFormatada + "\r\n"
                    + "Server: JOAOH/1.0\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
                //cria o canal de resposta utilizando o outputStream
                OutputStream resposta = socket.getOutputStream();
                //escreve o headers em bytes
                resposta.write(header.getBytes());
                //escreve o conteudo em bytes
                resposta.write(conteudo);
                //encerra a resposta e limpa o lixo
                resposta.flush();
                //encerra o buffer (Leitor)
                buffer.close();
                //encerra o socket, para liberar uma nova conexão
                socket.close();
            }
        }
    }
}