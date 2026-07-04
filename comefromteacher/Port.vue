<template>
    <!-- 图表容器必须指定宽高 -->
    <div ref="chartRef" style="width: 100%; height: 400px;"></div>
</template>

<script setup>
    import axios from 'axios'
    import {reactive,ref,onMounted} from 'vue'
    // 完整引入 echarts
    import * as echarts from 'echarts'

    const data = reactive({
        form:{
            userId: '',
            year: 2026
        },
        records: []

    })
    data.form.userId = JSON.parse(sessionStorage.getItem("user")).id
    const getData = ()=>{
        let path = 'http://localhost:8080/orders/selByMonth'
        axios.post(path,data.form).then(res=>{
            data.records = res.data
            console.log(data.records)
        })
    }
    getData()

    const chartRef = ref(null)
    // 渲染图表
    const renderChart = () => {
        // 实例化
        let myChart = echarts.init(chartRef.value)
        // 配置项
        const option = {
            title: { text: 'Vue3 ECharts 示例' },
            xAxis: {
                type: 'category',
                data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
            },
            yAxis: { type: 'value' },
            series: [
                {
                    name: '销量',
                    type: 'bar',
                    data: [120, 200, 150, 80, 70, 110, 130]
                }
            ]
        }
        // 设置配置
        myChart.setOption(option)
    }
    onMounted(()=>{
        renderChart()
    })

</script>

<style scoped>

</style>