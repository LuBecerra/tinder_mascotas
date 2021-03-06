package edu.egg.libreria.servicios;

import edu.egg.libreria.entidades.Foto;
import edu.egg.libreria.entidades.Mascota;
import edu.egg.libreria.entidades.Usuario;
import edu.egg.libreria.enumeracion.Sexo;
import edu.egg.libreria.errores.ErrorServicio;
import edu.egg.libreria.repositorios.MascotaRepositorio;
import edu.egg.libreria.repositorios.UsuarioRepositorio;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class MascotaServicio {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
    
    @Transactional
    public void agregarMascota(MultipartFile archivo,String idUsuario, String nombre,
            Sexo sexo) throws ErrorServicio, IOException{
        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        validar(nombre,sexo);
        
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setAlta(new Date());
        mascota.setUsuario(usuario);
        
        Foto foto = fotoServicio.guardar(archivo);
        mascota.setFoto(foto);
        
        mascotaRepositorio.save(mascota);
    }
    @Transactional
    public void modificar(MultipartFile archivo,String idUsuario, String idMascota,
            String nombre, Sexo sexo) throws ErrorServicio{
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if(mascota.getUsuario().getId().equals(idUsuario)){
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                
                String idFoto=null;
                if(mascota.getFoto() != null){
                    idFoto = mascota.getFoto().getId();
                }
                
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                mascota.setFoto(foto);
                
                mascotaRepositorio.save(mascota);
            }else{
                throw new ErrorServicio("La mascota no esta vinculada al usuario");
            }
        }else {
            throw new ErrorServicio("No existe una mascota con esa identificación");
        }        
    }
    @Transactional
    public void eliminar(String idUsuario, String idMascota) throws ErrorServicio{
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if(mascota.getUsuario().getId().equals(idUsuario)){
               mascota.setBaja(new Date());
               mascotaRepositorio.save(mascota);
            }else{
                throw new ErrorServicio("La mascota no esta vinculada al usuario");
            }
        }else {
            throw new ErrorServicio("No existe una mascota con esa identificación");
        } 
    }
    
    public void validar(String nombre, Sexo sexo) throws ErrorServicio{
        if(nombre == null || nombre.isEmpty()){
            throw new ErrorServicio("El nombre no puede estar vacío");
        }
        if(sexo == null){
            throw new ErrorServicio("El sexo no puede estar vacío");
        }
    }
    
}
