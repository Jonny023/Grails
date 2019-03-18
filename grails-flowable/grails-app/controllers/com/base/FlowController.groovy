package com.base

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import grails.io.IOUtils
import org.activiti.editor.language.json.converter.BpmnJsonConverter
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.commons.lang3.StringUtils
import org.flowable.bpmn.converter.BpmnXMLConverter
import org.flowable.bpmn.model.BpmnModel
import org.flowable.engine.repository.Deployment
import org.flowable.engine.repository.Model
import org.flowable.engine.runtime.ProcessInstance

class FlowController {

    def baseService

    private ObjectMapper objectMapper = new ObjectMapper()

    def index() {

    }

    def get() {
        def record = request.JSON ?: params
        ObjectNode modelNode = null
        Model model = baseService.repositoryService.getModel(record.id)
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo())
                } else {
                    modelNode = objectMapper.createObjectNode()
                    modelNode.put("name", model.getName())
                }
                modelNode.put("modelId", model.getId())
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(baseService.repositoryService.getModelEditorSource(model.getId()), "utf-8"))
                modelNode.put("model",editorJsonNode)

            } catch (Exception e) {
                log.error("not found model info.")
            }
        }
        render modelNode

    }

    /**
     *  保存修改流程
     * @return
     */
    def save() {
        def record =  params
        try {
            Model model = baseService.repositoryService.getModel(record.id)
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo())
            modelJson.put("name", record.name);
            modelJson.put("description", record.description)
            model.setMetaInfo(modelJson.toString())
            model.setName(record.name)
            baseService.repositoryService.saveModel(model)
            baseService.repositoryService.addModelEditorSource(model.getId(), record.json_xml.getBytes("utf-8"))
            InputStream svgStream = new ByteArrayInputStream(record.svg_xml.getBytes("utf-8"))
            TranscoderInput input = new TranscoderInput(svgStream)
            PNGTranscoder transcoder = new PNGTranscoder()
            ByteArrayOutputStream outStream = new ByteArrayOutputStream()
            TranscoderOutput output = new TranscoderOutput(outStream)
            transcoder.transcode(input, output)
            final byte[] result = outStream.toByteArray()
            baseService.repositoryService.addModelEditorSourceExtra(model.getId(), result)
            outStream.close()
        } catch(Exception e){
            log.error("Error saving model", e)
        }
        render true

    }

    /**
     *  获取汉化菜单
     * @return
     */
//    def getMenu() {
//        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json")
//        try {
//            render IOUtils.toString(stencilsetStream, "utf-8")
//        } catch (Exception e) {
//            log.error("Error while loading stencil set", e)
//        }
//    }

    /** 新建一个空模型 */
    def newModel(){
        def record = request.JSON ?: params
        def modelId = []
        //初始化一个空模型
        Model model = baseService.repositoryService.newModel()
        //设置一些默认信息
        int revision = 1
        String key = "process"
        ObjectNode modelNode = objectMapper.createObjectNode()
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, record.name)
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, record.description)
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision)
        model.setName(record.name)
        model.setKey(key)
        model.setMetaInfo(modelNode.toString())
        baseService.repositoryService.saveModel(model)
        String id = model.getId()
        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode()
        editorNode.put("id", "canvas")
        editorNode.put("resourceId", "canvas")
        ObjectNode stencilSetNode = objectMapper.createObjectNode()
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#")
        editorNode.put("stencilset", stencilSetNode)
        baseService.repositoryService.addModelEditorSource(id,editorNode.toString().getBytes("utf-8"))
        modelId.add(id)
        render modelId
    }
    /** 获取所有模型 */
    def modelList(){
        render baseService.repositoryService.createModelQuery().list() as JSON
    }
    /** 发布模型为流程定义 */
    def deploy(){
        def record = request.JSON ?: params
        //获取模型
        Model modelData = baseService.repositoryService.getModel(record.id)
        byte[] bytes = baseService.repositoryService.getModelEditorSource(modelData.getId())
        if (bytes == null) {
            render "模型数据为空，请先设计流程并成功保存，再进行发布"
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes)
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode)
        if(model.getProcesses().size()==0){
            render "数据模型不符要求，请至少设计一条主线流程"
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model)
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml"
        Deployment deployment = baseService.repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy()
        modelData.setDeploymentId(deployment.getId())
        baseService.repositoryService.saveModel(modelData)
        render true
    }
    /**启动流程*/
    def startProcess() {
        def record = request.JSON ?: params
        ProcessInstance process = baseService.runtimeService.startProcessInstanceByKey(record.keyName)
        render process.getId() + " : " + process.getProcessDefinitionId()
    }
}
