package com.twtchnz.gdxone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MyGdxGame extends Game {

    private AssetManager assets;
    private Sphere sphere;
    private boolean loading;
    private ModelBatch modelBatch;
    private Environment environment;
    private Camera camera;
    private CameraInputController cameraController;

    private Array<Sphere> spheres = new Array<Sphere>();
    private Array<Block> blocks = new Array<Block>();
    private Array<ModelInstance> toruses = new Array<ModelInstance>();
    private Array<Floor> floors = new Array<Floor>();
    private Array<Wall> walls = new Array<Wall>();

    private static final int TARGET_FPS = 30;
    private long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    private double deltaTime;
    private long lastTime;


	@Override
	public void create() {
        modelBatch = new ModelBatch();
        camera = new Camera();
        camera.update();

        loadModels();

        cameraController = new CameraInputController(camera.get());
        Gdx.input.setInputProcessor(cameraController);


	}

    private void loadModels() {
        assets = new AssetManager();
        assets.load("data/level1.g3db", Model.class);

        loading = true;
    }

    private void loadWorld() {
        loadEnvironment();

        Model model = assets.get("data/level1.g3db", Model.class);
        for(int i = 0; i < model.nodes.get(0).children.size; i++) {
            String id = model.nodes.get(0).children.get(i).id;

            if(id.startsWith("pCube")) {
                Block block = new Block(model, id);
                blocks.add(block);
            }
            else if(id.startsWith("pSphere")) {
                sphere = new Sphere(model, id);
                spheres.add(sphere);
            }
            else if(id.startsWith("wall")) {
                Wall wall = new Wall(model, id);
                walls.add(wall);
            }
            else if(id.startsWith("pPlane")) {
                System.out.print(id);
                Floor floor = new Floor(model, id);
                floors.add(floor);
            }
        }

        loading = false;
    }

    private void loadEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -3f, 2f));
    }
	@Override
	public void render() {
        if(loading && assets.update()){
            loadWorld();
            lastTime = System.nanoTime();
        }

        long currentTime = System.nanoTime();
        long updateTime  = currentTime - lastTime;
        lastTime = currentTime;
        deltaTime = updateTime / ((double)OPTIMAL_TIME);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        camera.update();

        modelBatch.begin(camera.get());
        for(Sphere sphere : spheres) {
            int i = 0;

            for(Block block : blocks){
                if(sphere.boundingBox.intersects(block.boundingBox)) {
                    Vector3 bounceVec = new Vector3();
                    float distance = calculateBounce(sphere, block.myNode, bounceVec);

                    Vector3 dimensions = new Vector3(sphere.boundingBox.getDimensions());

                    if(distance < dimensions.x || distance < dimensions.y || distance < dimensions.z) {
                        sphere.bounce(bounceVec);
                        blocks.removeIndex(i);
                    }

                }
                else{
                    block.update(deltaTime);
                    modelBatch.render(block, environment);
                }

                i++;
            }
            for(Wall wall : walls) {
                if(sphere.boundingBox.intersects(wall.boundingBox)) {
                    Vector3 bounceVec = new Vector3();
                    float distance = calculateBounce(sphere, wall.myNode, bounceVec);

                    Vector3 dimensions = new Vector3(sphere.boundingBox.getDimensions());

                    if(distance < dimensions.x || distance < dimensions.y || distance < dimensions.z) {
                        sphere.bounce(bounceVec);
                    }
                }

                modelBatch.render(wall, environment);
            }


            sphere.update(deltaTime);
            modelBatch.render(sphere, environment);
        }

        for(Floor floor : floors) {
            modelBatch.render(floor, environment);
        }

        modelBatch.end();
	}

    private float calculateBounce(Sphere sphere, Node myNode, Vector3 bounceVec) {
        MeshPart meshPart = myNode.parts.get(0).meshPart;

        Vector3 sphereDirection = new Vector3();
        sphereDirection.set(sphere.Velocity);

        Matrix4 transform = myNode.globalTransform;

        int numIndices = meshPart.mesh.getNumIndices();
        int indicesOffset = meshPart.indexOffset;
        int indicesCount = meshPart.numVertices;
        int indicesEnd = indicesOffset + indicesCount;

        final FloatBuffer verts = meshPart.mesh.getVerticesBuffer();
        final ShortBuffer indices = meshPart.mesh.getIndicesBuffer();

        final VertexAttribute norPosAttrib = meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal);
        final VertexAttribute verPosAttrib = meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Position);

        final int vertexSize = meshPart.mesh.getVertexAttributes().vertexSize / 4;

        final int norPosOff = norPosAttrib.offset / 4;
        final int verPosOff = verPosAttrib.offset / 4;

        Vector3 vertexVector1 = new Vector3();
        Vector3 vertexVector2 = new Vector3();
        Vector3 vertexVector3 = new Vector3();

        Vector3 normalVector = new Vector3();
        float distance = 0;
        Vector3 intersectionPoint = new Vector3();
        int count = 1;
        for(int j = indicesOffset; j < indicesEnd; j++) {
            final short tmpIndice = indices.get(j);
            final int norIndex = tmpIndice * vertexSize + norPosOff;
            final int verIndex = tmpIndice * vertexSize + verPosOff;

            if(count == 1)
                vertexVector1.set(verts.get(verIndex), verts.get(verIndex + 1), verts.get(verIndex + 2));
            else if(count == 2)
                vertexVector2.set(verts.get(verIndex), verts.get(verIndex + 1), verts.get(verIndex + 2));
            else if(count == 3) {
                vertexVector3.set(verts.get(verIndex), verts.get(verIndex + 1), verts.get(verIndex + 2));
                Vector3 tmpNormal = new Vector3(verts.get(norIndex), verts.get(norIndex + 1), verts.get(norIndex + 2));

                distance = intersect(sphere.boundingBox.getCenter(), sphereDirection, vertexVector1, vertexVector2, vertexVector3, intersectionPoint, normalVector, tmpNormal, distance);

                count = 0;
            }

            count++;

        }
        System.out.println(distance);
        Vector3 refVec = new Vector3();
        refVec.set(normalVector);
        float dot = sphere.Velocity.dot(normalVector);
        dot = -2 * dot;
        refVec.scl(dot).add(sphere.Velocity);
        bounceVec.set(refVec);


        return distance;
    }

    private float intersect(Vector3 point, Vector3 direction, Vector3 verVec1, Vector3 verVec2, Vector3 verVec3, Vector3 intPoint, Vector3 norVec, Vector3 tmpNor, float distance) {
        Vector3 tmpVec = new Vector3();
        Vector3 directions[] = new Vector3[3];
        directions[0] = direction;
        directions[1] = new Vector3(-(direction.z), direction.y, direction.x);
        directions[2] = new Vector3(direction.z, direction.y, -(direction.x));

        for(int i = 0; i < directions.length; i++) {
            Ray sphereRay = new Ray(point, directions[i]);
            Intersector.intersectRayTriangle(sphereRay, verVec1, verVec2, verVec3, tmpVec);

            if(tmpVec.dst(point) < intPoint.dst(point) ||(intPoint.isZero() && !tmpVec.isZero())) {
                distance = tmpVec.dst(point);
                intPoint.set(tmpVec);
                norVec.set(tmpNor);
            }
        }

        return distance;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        blocks.clear();
        assets.dispose();
        spheres.clear();
        floors.clear();
        walls.clear();

    }

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
