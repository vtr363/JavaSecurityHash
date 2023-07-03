import javax.crypto.*;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;

public class Hash {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public String doHash(String nomeArquivo)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String conteudoArquivo = new String(Files.readAllBytes(Paths.get(nomeArquivo)));
        md.update(conteudoArquivo.getBytes());
        byte[] digest = md.digest();
        return(bytesToHex(digest).toLowerCase());
    }
    
    //função para realizar o hash dos arquivos da pasta
    public String doHashInFolder(String folderPath) throws NoSuchAlgorithmException, IOException {
    	
    	//pega o caminho de cada arquivo na pasta
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folderPath))) {
        	String arquivoSaida = "";
            for (Path path : directoryStream) {
            	
            	//verifica se é arquivo ou pasta
                if (Files.isRegularFile(path)) {
                	
                	//se for arquivo realiza o hash e salva em uma string
                    String pathArquivo = path.toString();
                    String hash = doHash(pathArquivo);
                    arquivoSaida += "\n{CaminhoArquivo: " + pathArquivo +",\n"
                    				 + "NomeArquivo: " + path.getFileName().toString()+",\n"
                    				 + "Hash: " + hash+"}";
                    
                }
                else if(Files.isDirectory(path)) {
                	
                	//se for pasta chama novamente a função de forma recursiva
                	String pathPasta = path.toString();
                	String hash = doHashInFolder(pathPasta);
                	arquivoSaida += "\n{NomePasta: " + path.getFileName().toString() + ",\n"
                				  + "HashPasta: \n{" + hash + "\n}";
                }
            }
            // retorna todo para ser salvo em um txt a parte
            return arquivoSaida;
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String folderPath = ".\\src\\testeHash"; // Insira o caminho para a pasta aqui
        Files.writeString(Path.of("listaDeHash.txt"), (new Hash()).doHashInFolder(folderPath));;
    }
}
