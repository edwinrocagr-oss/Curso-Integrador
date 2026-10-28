package pe.edu.utp.cafegest.service;
import pe.edu.utp.cafegest.config.Database;
import java.sql.*;
import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/** Contraseñas con PBKDF2 y sal individual; nunca se guarda texto plano. */
public class AuthService {
    private static final int ITER=210000;
    public static String hash(String clave) {
        try {byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);byte[] bytes=derivar(clave,salt,ITER);return ITER+":"+Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(bytes);}catch(Exception e){throw new IllegalStateException(e);}
    }
    private static byte[] derivar(String clave,byte[] salt,int iter) throws Exception {
        PBEKeySpec spec=new PBEKeySpec(clave.toCharArray(),salt,iter,256);
        try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}finally{spec.clearPassword();}
    }
    public static boolean verificar(String clave,String guardado) {
        try {String[] p=guardado.split(":");if(p.length!=3)return false;return MessageDigest.isEqual(derivar(clave,Base64.getDecoder().decode(p[1]),Integer.parseInt(p[0])),Base64.getDecoder().decode(p[2]));}catch(Exception e){return false;}
    }
    public void crearAdminInicial(String clave) throws SQLException {
        if(clave==null||clave.length()<10)throw new IllegalArgumentException("La clave inicial debe tener 10 caracteres como mínimo");
        try(Connection c=Database.open();PreparedStatement count=c.prepareStatement("SELECT COUNT(*) FROM usuarios");ResultSet r=count.executeQuery()){
            r.next();if(r.getInt(1)>0)throw new IllegalStateException("Ya existen usuarios");
            try(PreparedStatement p=c.prepareStatement("INSERT INTO usuarios(usuario,clave_hash,rol) VALUES('admin',?,'ADMIN')")){p.setString(1,hash(clave));p.executeUpdate();}
        }
    }
    public boolean sinUsuarios() throws SQLException {try(Connection c=Database.open();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM usuarios")){r.next();return r.getInt(1)==0;}}
    public String login(String usuario,String clave) throws SQLException {
        try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("SELECT clave_hash,rol FROM usuarios WHERE usuario=? AND activo=TRUE")){
            p.setString(1,usuario);try(ResultSet r=p.executeQuery()){if(r.next()&&verificar(clave,r.getString(1)))return r.getString(2);return null;}
        }
    }
}
